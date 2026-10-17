//package com.example.merchantapp.presentation.signup
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.merchantapp.presentation.signup.validation.SignUpValidator
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.receiveAsFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//
//@HiltViewModel
//class SignUpViewModel @Inject constructor(): ViewModel() {
//
//    private val _state = MutableStateFlow(SignUpUiState())
//    val state: StateFlow<SignUpUiState> = _state.asStateFlow()
//
//    private val _effect = Channel<SignUpEffect>(Channel.BUFFERED)
//    val effect = _effect.receiveAsFlow()
//
//    fun onIntent(intent: SignUpIntent) {
//        when (intent) {
//            is SignUpIntent.UpdateFullName -> {
//                _state.update { it.copy(fullName = intent.value) }
//                updateNextButtonState()
//            }
//            is SignUpIntent.UpdateCountryCode -> {
//                _state.update { it.copy(countryCode = intent.code) }
//                updateNextButtonState()
//            }
//            is SignUpIntent.UpdatePhoneNumber -> {
//                if (intent.value.all { it.isDigit() }) {
//                    _state.update { it.copy(phoneNumber = intent.value) }
//                    updateNextButtonState()
//                }
//            }
//            is SignUpIntent.UpdateNationalId -> {
//                if (intent.value.all { it.isDigit() }) {
//                    _state.update { it.copy(nationalId = intent.value) }
//                    updateNextButtonState()
//                }
//            }
//            is SignUpIntent.UpdateTerms -> {
//                _state.update { it.copy(isTermsAccepted = intent.accepted) }
//                updateNextButtonState()
//            }
//
//            is SignUpIntent.UpdateBusinessName -> {
//                _state.update { it.copy(businessName = intent.value) }
//                updateNextButtonState()
//            }
//            is SignUpIntent.UpdateBusinessCategory -> {
//                _state.update { it.copy(businessCategory = intent.value) }
//                updateNextButtonState()
//            }
//            is SignUpIntent.UpdateBusinessAddress -> {
//                _state.update { it.copy(businessAddress = intent.value) }
//                updateNextButtonState()
//            }
//            is SignUpIntent.ToggleMapPickerDialog -> {
//                _state.update { it.copy(showMapPicker = intent.show) }
//            }
//            is SignUpIntent.UpdateBusinessWebsite -> {
//                _state.update { it.copy(businessWebsite = intent.value) }
//            }
//
//            is SignUpIntent.DownloadContractClicked -> {
//                val pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
//                _state.update { it.copy(isContractDownloaded = true) }
//                updateNextButtonState()
//                sendEffect(SignUpEffect.TriggerDownload(pdfUrl))
//            }
//
//            is SignUpIntent.UpdateSettlementType -> {
//                _state.update { it.copy(settlementType = intent.type) }
//                updateNextButtonState()
//            }
//            is SignUpIntent.UpdateBank -> {
//                _state.update { it.copy(selectedBank = intent.bank) }
//                updateNextButtonState()
//            }
//            is SignUpIntent.UpdateIban -> {
//                _state.update { it.copy(ibanNumber = intent.iban) }
//                updateNextButtonState()
//            }
//            is SignUpIntent.UpdateCardNumber -> {
//                _state.update { it.copy(cardNumber = intent.cardNumber) }
//                updateNextButtonState()
//            }
//            is SignUpIntent.UpdateWalletNumber -> {
//                _state.update { it.copy(walletNumber = intent.walletNumber) }
//                updateNextButtonState()
//            }
//
//            is SignUpIntent.UploadBankStatementClicked -> {
//                _state.update { it.copy(isBankStatementUploaded = true) }
//                updateNextButtonState()
//                sendEffect(SignUpEffect.ShowToast("Bank statement uploaded successfully"))
//            }
//
//            SignUpIntent.SaveAsDraftClicked -> {
//                sendEffect(SignUpEffect.ShowToast("Draft saved successfully"))
//            }
//
//            SignUpIntent.NextStepClicked -> {
//                val current = _state.value.currentStep
//                if (current < 8) {
//                    val nextStep = when (current) {
//                        2 -> 4
//                        4 -> 8
//                        else -> current + 1
//                    }
//                    _state.update { it.copy(currentStep = nextStep) }
//                    updateNextButtonState()
//                } else {
//                    sendEffect(SignUpEffect.SignUpCompleted)
//                }
//            }
//
//            SignUpIntent.PreviousStepClicked -> {
//                val current = _state.value.currentStep
//                if (current <= 2) {
//                    sendEffect(SignUpEffect.NavigateBack)
//                } else {
//                    val prevStep = when (current) {
//                        8 -> 4
//                        4 -> 2
//                        else -> current - 1
//                    }
//                    _state.update { it.copy(currentStep = prevStep) }
//                    updateNextButtonState()
//                }
//            }
//
//            SignUpIntent.ErrorDismissed -> {
//                _state.update { it.copy(status = SignUpStatus.Idle) }
//            }
//        }
//    }
//
//    private fun updateNextButtonState() {
//        val currentState = _state.value
//        val isValid = when (currentState.currentStep) {
//            2 -> SignUpValidator.isPersonalInfoValid(
//                currentState.fullName,
//                currentState.phoneNumber,
//                currentState.countryCode,
//                currentState.nationalId,
//                currentState.isTermsAccepted
//            )
//            4 -> SignUpValidator.isBusinessDetailsValid(
//                currentState.businessName,
//                currentState.businessCategory,
//                currentState.businessAddress,
//                currentState.isContractDownloaded
//            )
//            8 -> {
//                when (currentState.settlementType) {
//                    SettlementType.BANK_ACCOUNT ->
//                        currentState.selectedBank.isNotBlank() &&
//                                currentState.ibanNumber.isNotBlank() &&
//                                currentState.isBankStatementUploaded
//
//                    SettlementType.CARD ->
//                        currentState.cardNumber.isNotBlank() && currentState.cardNumber.length == 16
//
//                    SettlementType.WALLET ->
//                        currentState.walletNumber.isNotBlank() && currentState.walletNumber.length == 10
//                }
//            }
//            else -> true
//        }
//
//        _state.update { it.copy(isNextEnabled = isValid) }
//    }
//
//    private fun sendEffect(effect: SignUpEffect) {
//        viewModelScope.launch {
//            _effect.send(effect)
//        }
//    }
//}

package com.example.merchantapp.presentation.signup

import androidx.lifecycle.ViewModel
import com.example.merchantapp.presentation.signup.validation.SignUpValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SignUpUiState())
    val state: StateFlow<SignUpUiState> = _state.asStateFlow()

    private val _effect = Channel<SignUpEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: SignUpIntent) {
        when (intent) {

            // ---------- Personal information: Step 2 ----------

            is SignUpIntent.UpdateFullName -> {
                _state.update { it.copy(fullName = intent.value) }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateCountryCode -> {
                _state.update { it.copy(countryCode = intent.code) }
                updateNextButtonState()
            }

            is SignUpIntent.UpdatePhoneNumber -> {
                if (intent.value.all { it.isDigit() }) {
                    _state.update { it.copy(phoneNumber = intent.value) }
                    updateNextButtonState()
                }
            }

            is SignUpIntent.UpdateNationalId -> {
                if (intent.value.all { it.isDigit() }) {
                    _state.update { it.copy(nationalId = intent.value) }
                    updateNextButtonState()
                }
            }

            is SignUpIntent.UpdateTerms -> {
                _state.update { it.copy(isTermsAccepted = intent.accepted) }
                updateNextButtonState()
            }

            // ---------- Business details: Step 4 ----------

            is SignUpIntent.UpdateBusinessName -> {
                _state.update { it.copy(businessName = intent.value) }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateBusinessCategory -> {
                _state.update { it.copy(businessCategory = intent.value) }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateBusinessAddress -> {
                _state.update { it.copy(businessAddress = intent.value) }
                updateNextButtonState()
            }

            is SignUpIntent.ToggleMapPickerDialog -> {
                _state.update { it.copy(showMapPicker = intent.show) }
            }

            is SignUpIntent.UpdateBusinessWebsite -> {
                _state.update { it.copy(businessWebsite = intent.value) }
            }

            is SignUpIntent.DownloadContractClicked -> {
                val pdfUrl =
                    "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"


                _state.update { it.copy(isContractDownloaded = false) }

                sendEffect(SignUpEffect.TriggerDownload(pdfUrl))
                updateNextButtonState()
            }

            is SignUpIntent.ContractDownloadSucceeded -> {
                _state.update {
                    it.copy(
                        isContractDownloaded = true,
                        status = SignUpStatus.Idle
                    )
                }
                updateNextButtonState()
            }

            is SignUpIntent.ContractDownloadFailed -> {
                _state.update {
                    it.copy(
                        isContractDownloaded = false,
                        status = SignUpStatus.Error(intent.message)
                    )
                }
                updateNextButtonState()
                sendEffect(SignUpEffect.ShowToast(intent.message))
            }

            // ---------- Settlement: Step 8 ----------

            is SignUpIntent.UpdateSettlementType -> {
                _state.update { it.copy(settlementType = intent.type) }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateBank -> {
                _state.update { it.copy(selectedBank = intent.bank) }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateIban -> {
                _state.update { it.copy(ibanNumber = intent.iban) }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateCardNumber -> {
                if (intent.cardNumber.all { it.isDigit() }) {
                    _state.update { it.copy(cardNumber = intent.cardNumber) }
                    updateNextButtonState()
                }
            }

            is SignUpIntent.UpdateWalletNumber -> {
                if (intent.walletNumber.all { it.isDigit() }) {
                    _state.update { it.copy(walletNumber = intent.walletNumber) }
                    updateNextButtonState()
                }
            }

            is SignUpIntent.UploadBankStatementClicked -> {
                // فتح اختيار الملف يتم من الواجهة.
                // لا نعتبر الملف مرفوعًا قبل نجاح العملية الفعلية.
                sendEffect(SignUpEffect.SelectBankStatementFile)
            }

            is SignUpIntent.BankStatementUploadSucceeded -> {
                _state.update {
                    it.copy(
                        isBankStatementUploaded = true,
                        status = SignUpStatus.Idle
                    )
                }
                updateNextButtonState()
                sendEffect(
                    SignUpEffect.ShowToast(
                        "Bank statement uploaded successfully"
                    )
                )
            }

            is SignUpIntent.BankStatementUploadFailed -> {
                _state.update {
                    it.copy(
                        isBankStatementUploaded = false,
                        status = SignUpStatus.Error(intent.message)
                    )
                }
                updateNextButtonState()
                sendEffect(SignUpEffect.ShowToast(intent.message))
            }

            // ---------- Navigation ----------

            SignUpIntent.NextStepClicked -> {
                val currentStep = _state.value.currentStep


                if (!isCurrentStepValid()) {
                    updateNextButtonState()
                    sendEffect(
                        SignUpEffect.ShowToast(
                            "Please complete the required fields correctly"
                        )
                    )
                    return
                }

                when (currentStep) {
                    2 -> navigateToStep(4)
                    4 -> navigateToStep(8)

                    8 -> {
                        sendEffect(
                            SignUpEffect.ShowToast(
                                "Registration UI flow completed successfully"
                            )
                        )
                    }

                    else -> {
                        sendEffect(
                            SignUpEffect.ShowToast("Invalid registration step")
                        )
                    }
                }
            }

            SignUpIntent.PreviousStepClicked -> {
                when (_state.value.currentStep) {
                    8 -> navigateToStep(4)
                    4 -> navigateToStep(2)
                    2 -> sendEffect(SignUpEffect.NavigateBack)

                    else -> {
                        sendEffect(
                            SignUpEffect.ShowToast("Invalid registration step")
                        )
                    }
                }
            }

            // ---------- Save draft ----------

            SignUpIntent.SaveAsDraftClicked -> {
                sendEffect(
                    SignUpEffect.ShowToast(
                        "Draft saving is implemented"
                    )
                )
            }

            // ---------- Error handling ----------

            SignUpIntent.ErrorDismissed -> {
                _state.update { it.copy(status = SignUpStatus.Idle) }
            }
        }
    }


    private fun isCurrentStepValid(): Boolean {
        val currentState = _state.value

        return when (currentState.currentStep) {
            2 -> SignUpValidator.isPersonalInfoValid(
                fullName = currentState.fullName,
                phoneNumber = currentState.phoneNumber,
                countryCode = currentState.countryCode,
                nationalId = currentState.nationalId,
                termsAccepted = currentState.isTermsAccepted
            )

            4 -> SignUpValidator.isBusinessDetailsValid(
                businessName = currentState.businessName,
                businessCategory = currentState.businessCategory,
                businessAddress = currentState.businessAddress,
                isContractDownloaded = currentState.isContractDownloaded
            )

            8 -> SignUpValidator.isSettlementValid(currentState)

            else -> false
        }
    }


    private fun updateNextButtonState() {
        val isValid = isCurrentStepValid()



        _state.update {
            it.copy(isNextEnabled = isValid)
        }
    }

    private fun navigateToStep(step: Int) {
        if (step !in listOf(2, 4, 8)) return

        _state.update {
            it.copy(currentStep = step)
        }

        updateNextButtonState()
        sendEffect(SignUpEffect.NavigateToStep(step))
    }


    private fun sendEffect(effect: SignUpEffect) {
        _effect.trySend(effect)
    }
}
