package com.example.merchantapp.presentation.login

data class LoginUiState(
    val merchantId: String = "",
    val pin: String = "",
    val rememberMe: Boolean = false,
    val isContinueEnabled: Boolean = false,
    val selectedLanguage: String = "English",
    val status: LoginStatus = LoginStatus.Idle
)


sealed interface LoginStatus {
    data object Idle : LoginStatus
    data object Loading : LoginStatus
    data object Success : LoginStatus
    data class Error(val message: String) : LoginStatus
}