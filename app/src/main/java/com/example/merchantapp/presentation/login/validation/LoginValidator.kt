package com.example.merchantapp.presentation.login.validation

import com.example.merchantapp.core.constants.Constants
import javax.inject.Inject

class LoginValidator @Inject constructor() {

    fun validateForm(
        merchantId: String,
        pin: String
    ): Boolean {
        return merchantId.length == Constants.NATIONAL_ID_LENGTH &&
                merchantId.all { it.isDigit() } &&
                pin.length == Constants.PIN_LENGTH &&
                pin.all { it.isDigit() }
    }

    fun isValidPinInput(value: String): Boolean {
        return value.length <= Constants.PIN_LENGTH &&
                value.all { it.isDigit() }
    }
}