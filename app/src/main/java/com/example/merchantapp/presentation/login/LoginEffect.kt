
package com.example.merchantapp.presentation.login

sealed interface LoginEffect {
    data class NavigateToHome(
        val merchantId: String
    ) : LoginEffect

    data object NavigateToSignUp : LoginEffect
}
