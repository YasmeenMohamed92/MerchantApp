package com.example.merchantapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MerchantUserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MerchantDatabase : RoomDatabase() {

    abstract fun merchantUserDao(): MerchantUserDao
}