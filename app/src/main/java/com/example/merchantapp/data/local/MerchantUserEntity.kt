package com.example.merchantapp.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "merchants",
    indices = [
        Index(value = ["phoneNumber"], unique = true)
    ]
)
data class MerchantUserEntity(

    @PrimaryKey
    val nationalId: String,

    val phoneNumber: String
)