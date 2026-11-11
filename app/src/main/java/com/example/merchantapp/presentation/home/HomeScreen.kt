
package com.example.merchantapp.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.merchantapp.R
import com.example.merchantapp.ui.theme.MerchantBorder
import com.example.merchantapp.ui.theme.MerchantSecondaryText

@Composable
fun HomeScreen(
    merchantId: String,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(merchantId) {
        viewModel.loadProfile(merchantId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 16.dp,
                    vertical = 24.dp
                ),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.merchant_dashboard),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.merchant_dashboard_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MerchantSecondaryText
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage
                            ?: stringResource(
                                R.string.merchant_profile_load_error
                            ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.profile != null -> {
                    val profile = state.profile!!

                    Text(
                        text = stringResource(
                            R.string.merchant_welcome,
                            profile.fullName
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ProfileSection(
                        title = stringResource(
                            R.string.personal_information
                        )
                    ) {
                        ProfileRow(
                            stringResource(R.string.user_id),
                            profile.userId.toString()
                        )
                        ProfileRow(
                            stringResource(R.string.full_name),
                            profile.fullName
                        )
                        ProfileRow(
                            stringResource(R.string.country_code),
                            profile.countryCode
                        )
                        ProfileRow(
                            stringResource(R.string.phone_number),
                            profile.phoneNumber
                        )
                        ProfileRow(
                            stringResource(R.string.national_id),
                            profile.nationalId
                        )
                    }

                    ProfileSection(
                        title = stringResource(
                            R.string.business_information
                        )
                    ) {
                        ProfileRow(
                            stringResource(R.string.business_name),
                            profile.businessName
                        )
                        ProfileRow(
                            stringResource(R.string.business_category),
                            profile.businessCategory
                        )
                        ProfileRow(
                            stringResource(R.string.business_address),
                            profile.businessAddress
                        )
                        ProfileRow(
                            stringResource(R.string.business_website),
                            profile.businessWebsite
                        )
                    }

                    ProfileSection(
                        title = stringResource(
                            R.string.settlement_information
                        )
                    ) {
                        ProfileRow(
                            stringResource(R.string.settlement_type),
                            profile.settlementType
                        )

                        when (
                            profile.settlementType
                                .trim()
                                .replace(" ", "_")
                                .uppercase()
                        ) {
                            "BANK_ACCOUNT" -> {
                                ProfileRow(
                                    stringResource(R.string.selected_bank),
                                    profile.selectedBank
                                )
                                ProfileRow(
                                    stringResource(R.string.iban),
                                    profile.ibanNumber
                                )
                            }

                            "CARD" -> {
                                ProfileRow(
                                    stringResource(R.string.card_number),
                                    profile.cardNumber
                                )
                            }

                            "WALLET" -> {
                                ProfileRow(
                                    stringResource(R.string.wallet_number),
                                    profile.walletNumber
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(
            color = MerchantBorder,
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        content()

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(
            color = MerchantBorder,
            thickness = 1.dp
        )
    }
}

@Composable
private fun ProfileRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.42f),
            style = MaterialTheme.typography.bodyMedium,
            color = MerchantSecondaryText
        )

        Text(
            text = value.ifBlank { "—" },
            modifier = Modifier.weight(0.58f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
