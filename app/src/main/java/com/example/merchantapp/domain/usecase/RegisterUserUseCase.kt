
package com.example.merchantapp.domain.usecase

import com.example.merchantapp.core.constants.Constants
import com.example.merchantapp.domain.model.MerchantUser
import com.example.merchantapp.domain.repository.MerchantUserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class RegisterUserUseCase @Inject constructor(
    private val repository: MerchantUserRepository
) {

    operator fun invoke(
        merchant: MerchantUser
    ): Flow<Result<Unit>> = flow {

        if (merchant.fullName.isBlank()) {
            emit(
                Result.failure<Unit>(
                    IllegalArgumentException(
                        Constants.ERROR_FULL_NAME_REQUIRED
                    )
                )
            )
            return@flow
        }

        if (
            merchant.nationalId.length !=
            Constants.NATIONAL_ID_LENGTH ||
            !merchant.nationalId.all { it.isDigit() }
        ) {
            emit(
                Result.failure<Unit>(
                    IllegalArgumentException(
                        Constants.ERROR_INVALID_NATIONAL_ID
                    )
                )
            )
            return@flow
        }

        if (
            merchant.phoneNumber.length !=
            Constants.PHONE_NUMBER_INPUT_LENGTH ||
            !merchant.phoneNumber.all { it.isDigit() }
        ) {
            emit(
                Result.failure<Unit>(
                    IllegalArgumentException(
                        Constants.ERROR_INVALID_PHONE_NUMBER
                    )
                )
            )
            return@flow
        }

        if (!merchant.termsAccepted) {
            emit(
                Result.failure<Unit>(
                    IllegalArgumentException(
                        Constants.ERROR_TERMS_NOT_ACCEPTED
                    )
                )
            )
            return@flow
        }

        emitAll(repository.registerUser(merchant))
    }
}
