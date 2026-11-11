package com.example.merchantapp.presentation.signup

sealed interface SignUpEffect {
    data object NavigateBack : SignUpEffect

    data class SignUpCompleted(
        val merchantId: String
    ) : SignUpEffect
    data class ShowToast(val message: String) : SignUpEffect
    data class TriggerDownload(val pdfUrl: String) : SignUpEffect

    data class NavigateToStep(val step: Int) : SignUpEffect
    data object SelectBankStatementFile : SignUpEffect
}