package com.example.merchantapp.domain.model

data class MerchantUser(
    val fullName: String,
    val countryCode: String,
    val phoneNumber: String,
    val nationalId: String,
    val termsAccepted: Boolean,


    val businessName: String,
    val businessCategory: String,
    val businessAddress: String,
    val isContractDownloaded: Boolean = false,
    val businessWebsite: String,

    val settlementType: String,
    val selectedBank: String,
    val ibanNumber: String,
    val isBankStatementUploaded: Boolean = false,

    val cardNumber: String,
    val walletNumber: String
)