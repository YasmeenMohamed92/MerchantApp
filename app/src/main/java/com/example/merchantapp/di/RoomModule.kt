
package com.example.merchantapp.di

import android.content.Context
import androidx.room.Room
import com.example.merchantapp.data.local.MerchantDatabase
import com.example.merchantapp.data.local.MerchantUserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideMerchantDatabase(
        @ApplicationContext context: Context
    ): MerchantDatabase {
        return Room.databaseBuilder(
            context,
            MerchantDatabase::class.java,
            "merchant_local_database"
        )
            .addMigrations(
                MerchantDatabase.MIGRATION_1_2,
                MerchantDatabase.MIGRATION_2_3,
                MerchantDatabase.MIGRATION_3_4,
                MerchantDatabase.MIGRATION_4_5
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideMerchantUserDao(
        database: MerchantDatabase
    ): MerchantUserDao {
        return database.merchantUserDao()
    }
}
