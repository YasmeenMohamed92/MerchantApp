package com.example.merchantapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchantapp.domain.usecase.GetMerchantProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMerchantProfileUseCase: GetMerchantProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun loadProfile(merchantId: String) {
        viewModelScope.launch {
            _state.value = HomeUiState(isLoading = true)

            try {
                val profile = getMerchantProfileUseCase(merchantId)

                _state.value = if (profile != null) {
                    HomeUiState(
                        isLoading = false,
                        profile = profile
                    )
                } else {
                    HomeUiState(
                        isLoading = false,
                        errorMessage = "Merchant profile was not found."
                    )
                }
            } catch (exception: Exception) {
                _state.value = HomeUiState(
                    isLoading = false,
                    errorMessage = exception.message
                        ?: "Unable to load merchant profile."
                )
            }
        }
    }
}