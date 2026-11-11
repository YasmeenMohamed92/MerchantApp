package com.example.merchantapp.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "merchant_personal_info",
    indices = [
        Index(value = ["nationalId"], unique = true)
    ]
)
data class MerchantPersonalInfoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val fullName: String,
    val countryCode: String,
    val phoneNumber: String,
    val nationalId: String,
    val termsAccepted: Boolean
)