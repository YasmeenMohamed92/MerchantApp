package com.example.merchantapp.presentation.signup

sealed interface SignUpIntent {
    data class UpdateFullName(val value: String) : SignUpIntent
    data class UpdateCountryCode(val code: String) : SignUpIntent
    data class UpdatePhoneNumber(val value: String) : SignUpIntent
    data class UpdateNationalId(val value: String) : SignUpIntent
    data class UpdateTerms(val accepted: Boolean) : SignUpIntent

    // Business Details Intents
    data class UpdateBusinessName(val value: String) : SignUpIntent
    data class UpdateBusinessCategory(val value: String) : SignUpIntent
    data class UpdateBusinessAddress(val value: String) : SignUpIntent
    data class ToggleMapPickerDialog(val show: Boolean) : SignUpIntent
    data class UpdateBusinessWebsite(val value: String) : SignUpIntent
    data object DownloadContractClicked : SignUpIntent

    data object ContractDownloadSucceeded : SignUpIntent
    data class ContractDownloadFailed(val message: String) : SignUpIntent

    // Settlement Account Intents
    data class UpdateSettlementType(val type: SettlementType) : SignUpIntent
    data class UpdateBank(val bank: String) : SignUpIntent
    data class UpdateIban(val iban: String) : SignUpIntent
    data class UpdateCardNumber(val cardNumber: String) : SignUpIntent
    data class UpdateWalletNumber(val walletNumber: String) : SignUpIntent
    data object UploadBankStatementClicked : SignUpIntent

    data object BankStatementUploadSucceeded : SignUpIntent
    data class BankStatementUploadFailed(val message: String) : SignUpIntent

    // General Actions
    data object NextStepClicked : SignUpIntent
    data object PreviousStepClicked : SignUpIntent
    data object SaveAsDraftClicked : SignUpIntent
    data object ErrorDismissed : SignUpIntent

}