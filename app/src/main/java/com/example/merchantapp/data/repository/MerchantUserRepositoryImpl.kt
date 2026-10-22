package com.example.merchantapp.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.merchantapp.data.local.MerchantUserDao
import com.example.merchantapp.data.local.MerchantUserEntity
import com.example.merchantapp.domain.model.MerchantUser
import com.example.merchantapp.domain.repository.MerchantUserRepository
import javax.inject.Inject

class MerchantUserRepositoryImpl @Inject constructor(
    private val userDao: MerchantUserDao
) : MerchantUserRepository {

    override suspend fun registerUser(
        merchant: MerchantUser
    ): Result<Unit> {
        return try {
            userDao.insertMerchant(
                MerchantUserEntity(
                    nationalId = merchant.nationalId,
                    phoneNumber = merchant.phoneNumber
                )
            )

            Result.success(Unit)
        } catch (exception: SQLiteConstraintException) {
            Result.failure(
                IllegalArgumentException(
                    "This National ID or phone number is already registered."
                )
            )
        } catch (exception: Exception) {
            Result.failure(
                Exception(
                    exception.message
                        ?: "Unable to save registration."
                )
            )
        }
    }

    override suspend fun authenticate(
        merchantId: String,
        pin: String
    ): Boolean {
        return userDao.credentialsMatch(
            nationalId = merchantId,
            phoneNumber = pin
        )
    }
}