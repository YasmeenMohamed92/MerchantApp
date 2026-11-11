package com.example.merchantapp.domain.repository

import com.example.merchantapp.domain.model.MerchantProfile
import com.example.merchantapp.domain.model.MerchantUser
import kotlinx.coroutines.flow.Flow

interface MerchantUserRepository {

    fun registerUser(
        merchant: MerchantUser
    ): Flow<Result<Unit>>

    suspend fun authenticate(
        merchantId: String,
        pin: String
    ): Boolean

    suspend fun getMerchantProfile(
        merchantId: String
    ): MerchantProfile?

    suspend fun isNationalIdRegistered(
        nationalId: String
    ): Boolean
}