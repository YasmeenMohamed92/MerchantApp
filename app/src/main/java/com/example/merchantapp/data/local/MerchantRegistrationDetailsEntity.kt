package com.example.merchantapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "merchant_registration_details",
    foreignKeys = [
        ForeignKey(
            entity = MerchantPersonalInfoEntity::class,
            parentColumns = ["id"],
            childColumns = ["merchantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["merchantId"], unique = true)
    ]
)
data class MerchantRegistrationDetailsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val merchantId: Long,

    val businessName: String,
    val businessCategory: String,
    val businessAddress: String,

    val isContractDownloaded: Boolean = false,

    val businessWebsite: String,

    val settlementType: String,
    val selectedBank: String,
    val ibanNumber: String,

    // false = not uploaded, true = uploaded, null = not applicable
    val isBankStatementUploaded: Boolean?,

    val cardNumber: String,
    val walletNumber: String
)
