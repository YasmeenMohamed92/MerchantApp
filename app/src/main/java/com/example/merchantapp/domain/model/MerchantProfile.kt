package com.example.merchantapp.domain.model

data class MerchantProfile(
    val userId: Long,
    val fullName: String,
    val countryCode: String,
    val phoneNumber: String,
    val nationalId: String,
    val businessName: String,
    val businessCategory: String,
    val businessAddress: String,
    val businessWebsite: String,
    val settlementType: String,
    val selectedBank: String,
    val ibanNumber: String,
    val cardNumber: String,
    val walletNumber: String
)