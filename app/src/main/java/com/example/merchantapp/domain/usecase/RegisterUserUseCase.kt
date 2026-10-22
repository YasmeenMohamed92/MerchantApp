package com.example.merchantapp.domain.usecase

import com.example.merchantapp.domain.model.MerchantUser
import com.example.merchantapp.domain.repository.MerchantUserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: MerchantUserRepository
) {

    suspend operator fun invoke(
        nationalId: String,
        phoneNumber: String
    ): Result<Unit> {

        if (
            nationalId.length != 14 ||
            !nationalId.all { it.isDigit() }
        ) {
            return Result.failure(
                IllegalArgumentException(
                    "National ID must contain exactly 14 digits."
                )
            )
        }

        if (
            phoneNumber.length != 6 ||
            !phoneNumber.all { it.isDigit() }
        ) {
            return Result.failure(
                IllegalArgumentException(
                    "Phone number must contain exactly 6 digits."
                )
            )
        }

        return repository.registerUser(
            MerchantUser(
                nationalId = nationalId,
                phoneNumber = phoneNumber
            )
        )
    }
}