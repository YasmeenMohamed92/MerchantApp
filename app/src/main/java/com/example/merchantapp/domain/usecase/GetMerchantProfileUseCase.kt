package com.example.merchantapp.domain.usecase

import com.example.merchantapp.domain.model.MerchantProfile
import com.example.merchantapp.domain.repository.MerchantUserRepository
import javax.inject.Inject

class GetMerchantProfileUseCase @Inject constructor(
    private val repository: MerchantUserRepository
) {
    suspend operator fun invoke(
        merchantId: String
    ): MerchantProfile? {
        merchantId ?: return null
        return repository.getMerchantProfile(merchantId)
    }
}
