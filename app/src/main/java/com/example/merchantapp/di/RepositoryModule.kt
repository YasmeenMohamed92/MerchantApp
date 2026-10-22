package com.example.merchantapp.di

import com.example.merchantapp.data.repository.MerchantUserRepositoryImpl
import com.example.merchantapp.domain.repository.MerchantUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMerchantUserRepository(
        implementation: MerchantUserRepositoryImpl
    ): MerchantUserRepository
}