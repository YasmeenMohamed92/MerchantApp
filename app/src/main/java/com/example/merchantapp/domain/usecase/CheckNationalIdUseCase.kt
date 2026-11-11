package com.example.merchantapp.domain.usecase

import com.example.merchantapp.domain.repository.MerchantUserRepository
import javax.inject.Inject

class CheckNationalIdUseCase @Inject constructor(
    private val repository: MerchantUserRepository
) {
    suspend operator fun invoke(
        nationalId: String
    ): Boolean {
        return repository.isNationalIdRegistered(nationalId)
    }
}