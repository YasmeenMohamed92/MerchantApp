package com.example.merchantapp.domain.usecase
import jakarta.inject.Inject
data class LoginParams(
    val merchantId: String,
    val pin: String
)
class LoginUseCase @Inject constructor() {

    suspend operator fun invoke(params: LoginParams): Result<Unit> {

        val isValid = params.merchantId == "124525552522" && params.pin == "121111"

        return if (isValid) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Incorrect Merchant ID or PIN\nPlease check your credentials and try again "))
        }
    }
}