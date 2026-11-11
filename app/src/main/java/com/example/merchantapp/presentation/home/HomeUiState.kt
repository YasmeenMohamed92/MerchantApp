package com.example.merchantapp.presentation.home

import com.example.merchantapp.domain.model.MerchantProfile

data class HomeUiState(
    val isLoading: Boolean = true,
    val profile: MerchantProfile? = null,
    val errorMessage: String? = null
)