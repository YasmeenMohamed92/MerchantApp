package com.example.merchantapp.presentation.signup.validation

import com.example.merchantapp.core.constants.Constants
import com.example.merchantapp.presentation.signup.SettlementType
import com.example.merchantapp.presentation.signup.SignUpUiState

object SignUpValidator {

    fun isPersonalInfoValid(
        fullName: String,
        phoneNumber: String,
        countryCode: String,
        nationalId: String,
        termsAccepted: Boolean
    ): Boolean {
        val nameTrimmed = fullName.trim()
        val words = nameTrimmed.split("\\s+".toRegex())

        val isNameValid =
            words.size >= Constants.MIN_FULL_NAME_WORDS &&
                    nameTrimmed.all { it.isLetter() || it.isWhitespace() }

        val isPhoneValid =
            phoneNumber.length == Constants.PHONE_NUMBER_INPUT_LENGTH &&
                    phoneNumber.all { it.isDigit() }

        val isIdValid =
            nationalId.length == Constants.NATIONAL_ID_LENGTH &&
                    nationalId.all { it.isDigit() }

        return isNameValid && isPhoneValid && isIdValid && termsAccepted
    }

    fun isBusinessDetailsValid(
        businessName: String,
        businessCategory: String,
        businessAddress: String,
        isContractDownloaded: Boolean
    ): Boolean {
        return businessName.isNotBlank() &&
                businessCategory.isNotBlank() &&
                businessAddress.isNotBlank() &&
                isContractDownloaded
    }

    fun isSettlementValid(state: SignUpUiState): Boolean {
        return when (state.settlementType) {
            SettlementType.BANK_ACCOUNT -> {
                state.selectedBank.isNotBlank() &&
                        state.ibanNumber.isNotBlank() &&
                        state.isBankStatementUploaded
            }

            SettlementType.CARD -> {
                state.cardNumber.length == Constants.CARD_NUMBER_LENGTH &&
                        state.cardNumber.all { it.isDigit() }
            }

            SettlementType.WALLET -> {
                state.walletNumber.length == Constants.WALLET_NUMBER_LENGTH &&
                        state.walletNumber.all { it.isDigit() }
            }
        }
    }
}