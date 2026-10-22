package com.example.merchantapp.presentation.signup.validation

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
        val isNameValid = words.size >= 4 && nameTrimmed.all { it.isLetter() || it.isWhitespace() }

//        val isPhoneValid = when (countryCode) {
//            "+20" -> phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
//            else -> phoneNumber.length in 7..15 && phoneNumber.all { it.isDigit() }
//        }
        val isPhoneValid =
            phoneNumber.length == 6 &&
                    phoneNumber.all { it.isDigit() }
        val isIdValid = nationalId.length == 14 && nationalId.all { it.isDigit() }

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
                state.cardNumber.length == 16 &&
                        state.cardNumber.all { it.isDigit() }
            }

            SettlementType.WALLET -> {
                state.walletNumber.length == 10 &&
                        state.walletNumber.all { it.isDigit() }
            }
        }
    }
}



//    private fun isIbanValid(value: String): Boolean {
//        val iban = value.filterNot { it.isWhitespace() }.uppercase()
//
//        // تحقق مبدئي من شكل IBAN، ثم التحقق من checksum.
//        if (!Regex("^[A-Z]{2}\\d{2}[A-Z0-9]{11,30}$").matches(iban)) {
//            return false
//        }
//
//        val rearranged = iban.drop(4) + iban.take(4)
//        var remainder = 0
//
//        for (char in rearranged) {
//            val digits = if (char.isLetter()) {
//                (char.code - 'A'.code + 10).toString()
//            } else {
//                char.toString()
//            }
//
//            for (digit in digits) {
//                remainder = (remainder * 10 + digit.digitToInt()) % 97
//            }
//        }
//
//        return remainder == 1
//    }
//}
//    fun isSettlementValid(state: SignUpUiState): Boolean {
//        return when (state.settlementType) {
//            SettlementType.BANK_ACCOUNT ->
//                state.selectedBank.isNotBlank() &&
//                        isIbanValid(state.ibanNumber) &&
//                        state.isBankStatementUploaded
//
//            SettlementType.CARD ->
//                state.cardNumber.length == 16 &&
//                        state.cardNumber.all { it.isDigit() }
//
//            SettlementType.WALLET ->
//                state.walletNumber.length == 10 &&
//                        state.walletNumber.all { it.isDigit() }
//        }
//    }