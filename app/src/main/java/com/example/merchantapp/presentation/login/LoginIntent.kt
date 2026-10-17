package com.example.merchantapp.presentation.login

sealed interface LoginIntent {
    data class MerchantIdChanged(
        val value: String
    ) : LoginIntent
    data class PinChanged(
        val value: String
    ) : LoginIntent
    data class RememberMeChanged(
        val value: Boolean
    ) : LoginIntent
    data class LanguageChanged(
        val language: String
    ) : LoginIntent
    data object FaceVerificationClicked : LoginIntent
    data object ContinueClicked : LoginIntent

    data object SignUpClicked :LoginIntent

    data object ForgotPinClicked : LoginIntent

    data object MobileNumberClicked : LoginIntent

    data object ErrorDismissed : LoginIntent
}
