package com.example.merchantapp.presentation.signup

data class SignUpUiState(
    val currentStep: Int = 2,

    // Step 2: Personal Info
    val fullName: String = "",
    val countryCode: String = "+20",
    val phoneNumber: String = "",
    val nationalId: String = "",
    val isTermsAccepted: Boolean = false,

    // Step 4: Business Details
    val businessName: String = "",
    val businessCategory: String = "",
    val businessAddress: String = "",
    val showMapPicker: Boolean = false,
    val businessWebsite: String = "",
    val isContractDownloaded: Boolean = false,

    // Step 8: Settlement Account
    val settlementType: SettlementType = SettlementType.BANK_ACCOUNT,
    val selectedBank: String = "",
    val ibanNumber: String = "",
    val isBankStatementUploaded: Boolean = false,

    val cardNumber: String = "",
    val walletNumber: String = "",

    // Common Status
    val status: SignUpStatus = SignUpStatus.Idle,
    val isNextEnabled: Boolean = false
)

enum class SettlementType {
    BANK_ACCOUNT, CARD, WALLET
}

sealed interface SignUpStatus {
    data object Idle : SignUpStatus
    data object Loading : SignUpStatus
    data object Success : SignUpStatus
    data class Error(val message: String) : SignUpStatus
}