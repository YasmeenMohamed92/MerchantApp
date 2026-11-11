package com.example.merchantapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface MerchantUserDao {

    @Insert
    suspend fun insertPersonalInfo(
        personalInfo: MerchantPersonalInfoEntity
    ): Long

    @Insert
    suspend fun insertRegistrationDetails(
        details: MerchantRegistrationDetailsEntity
    )

    @Transaction
    suspend fun insertMerchant(
        personalInfo: MerchantPersonalInfoEntity,
        details: MerchantRegistrationDetailsEntity
    ) {
        val generatedMerchantId =
            insertPersonalInfo(personalInfo)

        insertRegistrationDetails(
            details.copy(
                merchantId = generatedMerchantId
            )
        )
    }

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM merchant_personal_info
            WHERE nationalId = :nationalId
        )
        """
    )
    suspend fun isNationalIdRegistered(
        nationalId: String
    ): Boolean

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM merchant_personal_info
            WHERE nationalId = :nationalId
              AND phoneNumber = :phoneNumber
        )
        """
    )
    suspend fun credentialsMatch(
        nationalId: String,
        phoneNumber: String
    ): Boolean

    @Query(
        """
        SELECT id
        FROM merchant_personal_info
        WHERE nationalId = :nationalId
        LIMIT 1
        """
    )
    suspend fun getUserIdByNationalId(
        nationalId: String
    ): Long?

    @Query(
        """
        SELECT
            personal.id AS userId,
            personal.fullName AS fullName,
            personal.countryCode AS countryCode,
            personal.phoneNumber AS phoneNumber,
            personal.nationalId AS nationalId,
            details.businessName AS businessName,
            details.businessCategory AS businessCategory,
            details.businessAddress AS businessAddress,
            details.businessWebsite AS businessWebsite,
            details.settlementType AS settlementType,
            details.selectedBank AS selectedBank,
            details.ibanNumber AS ibanNumber,
            details.cardNumber AS cardNumber,
            details.walletNumber AS walletNumber
        FROM merchant_personal_info AS personal
        INNER JOIN merchant_registration_details AS details
            ON details.merchantId = personal.id
        WHERE personal.id = :userId
        LIMIT 1
        """
    )
    suspend fun getMerchantProfile(
        userId: Long
    ): MerchantProfileProjection?
}