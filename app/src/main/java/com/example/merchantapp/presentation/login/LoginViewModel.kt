package com.example.merchantapp.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchantapp.domain.usecase.LoginParams
import com.example.merchantapp.domain.usecase.LoginUseCase
import com.example.merchantapp.presentation.login.validation.LoginValidator
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loginValidator: LoginValidator
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.MerchantIdChanged -> {
                _state.update {
                    it.copy(
                        merchantId = intent.value,
                        isContinueEnabled = loginValidator.validateForm(intent.value, it.pin)
                    )
                }
            }
            is LoginIntent.PinChanged -> {
                if (loginValidator.isValidPinInput(intent.value)) {
                    _state.update {
                        it.copy(
                            pin = intent.value,
                            isContinueEnabled = loginValidator.validateForm(it.merchantId, intent.value)
                        )
                    }
                }
            }
            is LoginIntent.RememberMeChanged -> {
                _state.update { it.copy(rememberMe = intent.value) }
            }
            is LoginIntent.LanguageChanged -> {
                _state.update { it.copy(selectedLanguage = intent.language) }
            }
            LoginIntent.ContinueClicked -> {
                login()
            }
            LoginIntent.SignUpClicked -> {
                sendEffect(LoginEffect.NavigateToSignUp)
            }
            LoginIntent.ErrorDismissed -> {
                dismissError()
            }
            else -> {}
        }
    }

    private fun login() {
        val currentState = _state.value
        if (!loginValidator.validateForm(currentState.merchantId, currentState.pin)) return

        viewModelScope.launch {
            _state.update { it.copy(status = LoginStatus.Loading) }

            val params = LoginParams(merchantId = currentState.merchantId, pin = currentState.pin)

            loginUseCase(params)
                .onSuccess {
                    _state.update { it.copy(status = LoginStatus.Success) }
                    sendEffect(LoginEffect.NavigateToHome)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            status = LoginStatus.Error(
                                message = error.message ?: "Unable to login"
                            )
                        )
                    }
                }
        }
    }

    private fun dismissError() {
        _state.update { it.copy(status = LoginStatus.Idle) }
    }

    private fun sendEffect(effect: LoginEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}