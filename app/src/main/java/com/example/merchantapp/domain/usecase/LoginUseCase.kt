
package com.example.merchantapp.domain.usecase

import com.example.merchantapp.domain.model.LoginError
import com.example.merchantapp.domain.repository.MerchantUserRepository
import javax.inject.Inject

class LoginException(
    val error: LoginError
) : Exception()

data class LoginParams(
    val merchantId: String,
    val pin: String
)

class LoginUseCase @Inject constructor(
    private val repository: MerchantUserRepository
) {

    suspend operator fun invoke(
        params: LoginParams
    ): Result<Unit> {
        return try {
            val isValid = repository.authenticate(
                merchantId = params.merchantId,
                pin = params.pin
            )

            if (isValid) {
                Result.success(Unit)
            } else {
                Result.failure(
                    LoginException(
                        LoginError.INVALID_CREDENTIALS
                    )
                )
            }
        } catch (exception: Exception) {
            Result.failure(
                LoginException(
                    LoginError.UNABLE_TO_LOGIN
                )
            )
        }
    }
}
