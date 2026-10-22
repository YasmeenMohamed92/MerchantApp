package com.example.merchantapp.domain.repository

import com.example.merchantapp.domain.model.MerchantUser

interface MerchantUserRepository {

    suspend fun registerUser(
        merchant: MerchantUser
    ): Result<Unit>

    suspend fun authenticate(
        merchantId: String,
        pin: String
    ): Boolean
}