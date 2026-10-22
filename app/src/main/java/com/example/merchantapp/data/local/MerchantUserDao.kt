package com.example.merchantapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MerchantUserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMerchant(
        merchant: MerchantUserEntity
    )

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM merchants
            WHERE nationalId = :nationalId
              AND phoneNumber = :phoneNumber
        )
        """
    )
    suspend fun credentialsMatch(
        nationalId: String,
        phoneNumber: String
    ): Boolean
}