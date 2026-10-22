//package com.example.merchantapp.domain.usecase
//import jakarta.inject.Inject
//data class LoginParams(
//    val merchantId: String,
//    val pin: String
//)
//class LoginUseCase @Inject constructor() {
//
//    suspend operator fun invoke(params: LoginParams): Result<Unit> {
//
//        val isValid = params.merchantId == "124525552522" && params.pin == "121111"
//
//        return if (isValid) {
//            Result.success(Unit)
//        } else {
//            Result.failure(Exception("Incorrect Merchant ID or PIN\nPlease check your credentials and try again "))
//        }
//    }
//}
package com.example.merchantapp.domain.usecase

import com.example.merchantapp.domain.repository.MerchantUserRepository
import javax.inject.Inject

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
//            val isValid = repository.authenticate(
//                nationalId = params.merchantId,
//                phoneNumber = params.pin
//            )
            val isValid = repository.authenticate(
                merchantId = params.merchantId,
                pin = params.pin
            )

            if (isValid) {
                Result.success(Unit)
            } else {
                Result.failure(
                    IllegalArgumentException(
                        "Incorrect Merchant ID or PIN. Please check your credentials and try again."
                    )
                )
            }
        } catch (exception: Exception) {
            Result.failure(
                Exception(
                    exception.message
                        ?: "Unable to login. Please try again."
                )
            )
        }
    }
}