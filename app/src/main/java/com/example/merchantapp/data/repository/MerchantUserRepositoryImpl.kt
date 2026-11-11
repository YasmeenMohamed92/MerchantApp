package com.example.merchantapp.data.repository

import com.example.merchantapp.data.local.MerchantPersonalInfoEntity
import com.example.merchantapp.data.local.MerchantRegistrationDetailsEntity
import com.example.merchantapp.data.local.MerchantUserDao
import com.example.merchantapp.domain.model.MerchantProfile
import com.example.merchantapp.domain.model.MerchantUser
import com.example.merchantapp.domain.repository.MerchantUserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class MerchantUserRepositoryImpl @Inject constructor(
    private val userDao: MerchantUserDao
) : MerchantUserRepository {

    override fun registerUser(
        merchant: MerchantUser
    ): Flow<Result<Unit>> = flow {

        val normalizedSettlementType = merchant.settlementType
            .trim()
            .replace(" ", "_")
            .uppercase()

        val bankStatementStatus: Boolean? =
            when (normalizedSettlementType) {
                "BANK_ACCOUNT" -> merchant.isBankStatementUploaded
                else -> null
            }

        val personalInfo = MerchantPersonalInfoEntity(
            fullName = merchant.fullName,
            countryCode = merchant.countryCode,
            phoneNumber = merchant.phoneNumber,
            nationalId = merchant.nationalId,
            termsAccepted = merchant.termsAccepted
        )

        val registrationDetails = MerchantRegistrationDetailsEntity(
            merchantId = 0L,
            businessName = merchant.businessName,
            businessCategory = merchant.businessCategory,
            businessAddress = merchant.businessAddress,
            isContractDownloaded = merchant.isContractDownloaded,
            businessWebsite = merchant.businessWebsite,
            settlementType = merchant.settlementType,
            selectedBank = merchant.selectedBank,
            ibanNumber = merchant.ibanNumber,
            isBankStatementUploaded = bankStatementStatus,
            cardNumber = merchant.cardNumber,
            walletNumber = merchant.walletNumber
        )

        userDao.insertMerchant(
            personalInfo = personalInfo,
            details = registrationDetails
        )

        emit(Result.success(Unit))
    }.catch { exception ->
        emit(Result.failure(exception))
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

    override suspend fun getMerchantProfile(
        merchantId: String
    ): MerchantProfile? {
        val userId = userDao.getUserIdByNationalId(merchantId)
            ?: return null

        val profile = userDao.getMerchantProfile(userId)
            ?: return null

        return MerchantProfile(
            userId = profile.userId,
            fullName = profile.fullName,
            countryCode = profile.countryCode,
            phoneNumber = profile.phoneNumber,
            nationalId = profile.nationalId,
            businessName = profile.businessName,
            businessCategory = profile.businessCategory,
            businessAddress = profile.businessAddress,
            businessWebsite = profile.businessWebsite,
            settlementType = profile.settlementType,
            selectedBank = profile.selectedBank,
            ibanNumber = profile.ibanNumber,
            cardNumber = profile.cardNumber,
            walletNumber = profile.walletNumber
        )
    }

    override suspend fun isNationalIdRegistered(
        nationalId: String
    ): Boolean {
        return userDao.isNationalIdRegistered(nationalId)
    }
}