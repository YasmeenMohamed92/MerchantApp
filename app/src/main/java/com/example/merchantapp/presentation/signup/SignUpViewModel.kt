package com.example.merchantapp.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchantapp.domain.model.MerchantUser
import com.example.merchantapp.domain.usecase.CheckNationalIdUseCase
import com.example.merchantapp.domain.usecase.RegisterUserUseCase
import com.example.merchantapp.presentation.signup.validation.SignUpValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val checkNationalIdUseCase: CheckNationalIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpUiState())
    val state: StateFlow<SignUpUiState> = _state.asStateFlow()

    private val _effect = Channel<SignUpEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: SignUpIntent) {
        when (intent) {

            // Personal information: Step 2

            is SignUpIntent.UpdateFullName -> {
                _state.update {
                    it.copy(fullName = intent.value)
                }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateCountryCode -> {
                _state.update {
                    it.copy(countryCode = intent.code)
                }
                updateNextButtonState()
            }

            is SignUpIntent.UpdatePhoneNumber -> {
                if (
                    intent.value.length <= 6 &&
                    intent.value.all { it.isDigit() }
                ) {
                    _state.update {
                        it.copy(phoneNumber = intent.value)
                    }
                    updateNextButtonState()
                }
            }

            is SignUpIntent.UpdateNationalId -> {
                if (
                    intent.value.length <= 14 &&
                    intent.value.all { it.isDigit() }
                ) {
                    _state.update {
                        it.copy(nationalId = intent.value)
                    }
                    updateNextButtonState()
                }
            }

            is SignUpIntent.UpdateTerms -> {
                _state.update {
                    it.copy(isTermsAccepted = intent.accepted)
                }
                updateNextButtonState()
            }

            // Business details: Step 4

            is SignUpIntent.UpdateBusinessName -> {
                _state.update {
                    it.copy(businessName = intent.value)
                }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateBusinessCategory -> {
                _state.update {
                    it.copy(businessCategory = intent.value)
                }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateBusinessAddress -> {
                _state.update {
                    it.copy(businessAddress = intent.value)
                }
                updateNextButtonState()
            }

            is SignUpIntent.ToggleMapPickerDialog -> {
                _state.update {
                    it.copy(showMapPicker = intent.show)
                }
            }

            is SignUpIntent.UpdateBusinessWebsite -> {
                _state.update {
                    it.copy(businessWebsite = intent.value)
                }
            }

            is SignUpIntent.DownloadContractClicked -> {
                val pdfUrl =
                    "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"

                _state.update {
                    it.copy(isContractDownloaded = false)
                }

                sendEffect(
                    SignUpEffect.TriggerDownload(pdfUrl)
                )
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

                sendEffect(
                    SignUpEffect.ShowToast(intent.message)
                )
            }

            // Settlement account: Step 8

            is SignUpIntent.UpdateSettlementType -> {
                _state.update {
                    it.copy(settlementType = intent.type)
                }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateBank -> {
                _state.update {
                    it.copy(selectedBank = intent.bank)
                }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateIban -> {
                _state.update {
                    it.copy(ibanNumber = intent.iban)
                }
                updateNextButtonState()
            }

            is SignUpIntent.UpdateCardNumber -> {
                if (intent.cardNumber.all { it.isDigit() }) {
                    _state.update {
                        it.copy(cardNumber = intent.cardNumber)
                    }
                    updateNextButtonState()
                }
            }

            is SignUpIntent.UpdateWalletNumber -> {
                if (intent.walletNumber.all { it.isDigit() }) {
                    _state.update {
                        it.copy(walletNumber = intent.walletNumber)
                    }
                    updateNextButtonState()
                }
            }

            is SignUpIntent.UploadBankStatementClicked -> {
                sendEffect(
                    SignUpEffect.SelectBankStatementFile
                )
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

                sendEffect(
                    SignUpEffect.ShowToast(intent.message)
                )
            }

            // Navigation

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
                    2 -> checkNationalIdAndNavigate()

                    4 -> navigateToStep(8)

                    8 -> submitRegistration()

                    else -> {
                        sendEffect(
                            SignUpEffect.ShowToast(
                                "Invalid registration step"
                            )
                        )
                    }
                }
            }

            SignUpIntent.PreviousStepClicked -> {
                when (_state.value.currentStep) {
                    8 -> navigateToStep(4)
                    4 -> navigateToStep(2)

                    2 -> {
                        sendEffect(SignUpEffect.NavigateBack)
                    }

                    else -> {
                        sendEffect(
                            SignUpEffect.ShowToast(
                                "Invalid registration step"
                            )
                        )
                    }
                }
            }

            // Save draft

            SignUpIntent.SaveAsDraftClicked -> {
                sendEffect(
                    SignUpEffect.ShowToast(
                        "Draft saving is not implemented yet"
                    )
                )
            }

            // Error handling

            SignUpIntent.ErrorDismissed -> {
                _state.update {
                    it.copy(status = SignUpStatus.Idle)
                }
            }
        }
    }
    private fun checkNationalIdAndNavigate() {
        val nationalId = _state.value.nationalId

        if (_state.value.status is SignUpStatus.Loading) {
            return
        }

        _state.update {
            it.copy(status = SignUpStatus.Loading)
        }

        viewModelScope.launch {
            try {
                val isRegistered =
                    checkNationalIdUseCase(nationalId)

                if (isRegistered) {
                    val message =
                        "This National ID is already registered."

                    _state.update {
                        it.copy(
                            status = SignUpStatus.Error(message)
                        )
                    }

                    sendEffect(
                        SignUpEffect.ShowToast(message)
                    )
                } else {
                    _state.update {
                        it.copy(status = SignUpStatus.Idle)
                    }

                    navigateToStep(4)
                }
            } catch (exception: Exception) {
                val message =
                    "Unable to verify National ID. Please try again."

                _state.update {
                    it.copy(
                        status = SignUpStatus.Error(message)
                    )
                }

                sendEffect(
                    SignUpEffect.ShowToast(message)
                )
            }
        }
    }



    private fun submitRegistration() {
        val currentState = _state.value

        if (currentState.status is SignUpStatus.Loading) {
            return
        }

        _state.update {
            it.copy(status = SignUpStatus.Loading)
        }

        viewModelScope.launch {
            registerUserUseCase(
                MerchantUser(
                    fullName = currentState.fullName.trim(),
                    countryCode = currentState.countryCode,
                    phoneNumber = currentState.phoneNumber,
                    nationalId = currentState.nationalId,
                    termsAccepted = currentState.isTermsAccepted,

                    businessName = currentState.businessName,
                    businessCategory = currentState.businessCategory,
                    businessAddress = currentState.businessAddress,
                    isContractDownloaded = currentState.isContractDownloaded,
                    businessWebsite = currentState.businessWebsite,

                    settlementType = currentState.settlementType.name,
                    selectedBank = currentState.selectedBank,
                    ibanNumber = currentState.ibanNumber,
                    isBankStatementUploaded =
                        currentState.isBankStatementUploaded,

                    cardNumber = currentState.cardNumber,
                    walletNumber = currentState.walletNumber
                )
            ).collect { result ->

                result.onSuccess {
                    _state.update {
                        it.copy(status = SignUpStatus.Success)
                    }

                    sendEffect(
                        SignUpEffect.SignUpCompleted(
                            merchantId = currentState.nationalId
                        )
                    )                }.onFailure { error ->
                    val message = error.message
                        ?: "Unable to complete registration."

                    _state.update {
                        it.copy(
                            status = SignUpStatus.Error(message)
                        )
                    }

                    sendEffect(
                        SignUpEffect.ShowToast(message)
                    )
                }
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
                isContractDownloaded =
                    currentState.isContractDownloaded
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

        sendEffect(
            SignUpEffect.NavigateToStep(step)
        )
    }

    private fun sendEffect(effect: SignUpEffect) {
        _effect.trySend(effect)
    }
}
