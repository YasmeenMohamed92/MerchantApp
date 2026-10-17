
package com.example.merchantapp.presentation.signup.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.merchantapp.presentation.signup.SettlementType
import com.example.merchantapp.presentation.signup.SignUpIntent
import com.example.merchantapp.presentation.signup.SignUpUiState
import com.example.merchantapp.ui.theme.MerchantBorder
import com.example.merchantapp.ui.theme.MerchantSecondaryText

private val banksList = listOf(
    "Al Rajhi Bank",
    "Saudi National Bank (SNB)",
    "Riyad Bank",
    "Alinma Bank",
    "Arab National Bank",
    "Bank Albilad"
)

@Composable
fun SettlementAccountScreen(
    state: SignUpUiState,
    onIntent: (SignUpIntent) -> Unit,
    onUploadClicked: () -> Unit
) {
    var isBankExpanded by remember { mutableStateOf(false) }

    val onSettlementTypeChange: (SettlementType) -> Unit = { type ->
        onIntent(SignUpIntent.UpdateSettlementType(type))
    }

    val onBankChange: (String) -> Unit = { bank ->
        onIntent(SignUpIntent.UpdateBank(bank))
        isBankExpanded = false
    }

    val onIbanChange: (String) -> Unit = { input ->
        if (input.all { it.isDigit() }) {
            onIntent(SignUpIntent.UpdateIban(input))
        }
    }

    val onCardNumberChange: (String) -> Unit = { input ->
        if (input.all { it.isDigit() }) {
            onIntent(SignUpIntent.UpdateCardNumber(input))
        }
    }

    val onWalletNumberChange: (String) -> Unit = { input ->
        if (input.all { it.isDigit() }) {
            onIntent(SignUpIntent.UpdateWalletNumber(input))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            SettlementAccountHeader(
                onBackClick = {
                    onIntent(SignUpIntent.PreviousStepClicked)
                },
                onSaveDraftClick = {
                    onIntent(SignUpIntent.SaveAsDraftClicked)
                }
            )

            SettlementProgress()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add Settlement Account",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Choose how you'd like to receive your payments.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MerchantSecondaryText
                )

                Spacer(modifier = Modifier.height(20.dp))

                SettlementTypeSelector(
                    selectedType = state.settlementType,
                    onTypeSelected = onSettlementTypeChange
                )

                Spacer(modifier = Modifier.height(20.dp))

                when (state.settlementType) {
                    SettlementType.BANK_ACCOUNT -> {
                        BankSelector(
                            selectedBank = state.selectedBank,
                            expanded = isBankExpanded,
                            onExpandedChange = {
                                isBankExpanded = it
                            },
                            onBankSelected = onBankChange
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        IbanField(
                            value = state.ibanNumber,
                            onValueChange = onIbanChange
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        BankStatementUploadButton(
                            isUploaded = state.isBankStatementUploaded,
                            onUploadClicked = onUploadClicked
                        )
                    }

                    SettlementType.CARD -> {
                        CardNumberField(
                            value = state.cardNumber,
                            onValueChange = onCardNumberChange
                        )
                    }

                    SettlementType.WALLET -> {
                        WalletNumberField(
                            value = state.walletNumber,
                            onValueChange = onWalletNumberChange
                        )
                    }
                }
            }
        }

        SettlementSubmitButton(
            enabled = state.isNextEnabled,
            onSubmitClick = {
                onIntent(SignUpIntent.NextStepClicked)
            }
        )
    }
}

@Composable
private fun SettlementAccountHeader(
    onBackClick: () -> Unit,
    onSaveDraftClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(
            horizontal = 24.dp,
            vertical = 16.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable(
                        onClick = onBackClick
                    )
                )

                Text(
                    text = "Step 8 of 8",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MerchantSecondaryText
                )
            }

            Text(
                text = "Save as draft",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable(
                    onClick = onSaveDraftClick
                )
            )
        }
    }
}

@Composable
private fun SettlementProgress() {
    LinearProgressIndicator(
        progress = { 8f / 8f },
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MerchantBorder.copy(alpha = 0.25f),
        strokeCap = StrokeCap.Butt
    )
}

@Composable
private fun SettlementTypeSelector(
    selectedType: SettlementType,
    onTypeSelected: (SettlementType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedType == SettlementType.BANK_ACCOUNT,
            onClick = {
                onTypeSelected(SettlementType.BANK_ACCOUNT)
            },
            label = {
                Text(
                    text = "Bank Account",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    modifier = Modifier.height(18.dp)
                )
            },
            modifier = Modifier.weight(1f),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                selectedLabelColor = MaterialTheme.colorScheme.primary,
                selectedLeadingIconColor = MaterialTheme.colorScheme.primary
            )
        )

        FilterChip(
            selected = selectedType == SettlementType.CARD,
            onClick = {
                onTypeSelected(SettlementType.CARD)
            },
            label = {
                Text(
                    text = "Card",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    modifier = Modifier.height(18.dp)
                )
            },
            modifier = Modifier.weight(1f),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                selectedLabelColor = MaterialTheme.colorScheme.primary,
                selectedLeadingIconColor = MaterialTheme.colorScheme.primary
            )
        )

        FilterChip(
            selected = selectedType == SettlementType.WALLET,
            onClick = {
                onTypeSelected(SettlementType.WALLET)
            },
            label = {
                Text(
                    text = "Wallet",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = null,
                    modifier = Modifier.height(18.dp)
                )
            },
            modifier = Modifier.weight(1f),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                selectedLabelColor = MaterialTheme.colorScheme.primary,
                selectedLeadingIconColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun BankSelector(
    selectedBank: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onBankSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Select your bank",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedBank,
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text("Choose your bank")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clickable {
                        onExpandedChange(true)
                    },
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.clickable {
                            onExpandedChange(true)
                        }
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = MerchantBorder,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledPlaceholderColor = MerchantSecondaryText
                )
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        onExpandedChange(true)
                    }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    onExpandedChange(false)
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth(0.88f)
            ) {
                banksList.forEach { bank ->
                    val isSelected = selectedBank == bank

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = bank,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        },
                        onClick = {
                            onBankSelected(bank)
                        },
                        modifier = Modifier.background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.15f
                                )
                            } else {
                                Color.Transparent
                            }
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun IbanField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = "IBAN number",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("Enter or paste IBAN number here")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MerchantBorder,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun BankStatementUploadButton(
    isUploaded: Boolean,
    onUploadClicked: () -> Unit
) {
    Column {
        Text(
            text = "Bank Statement",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onUploadClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isUploaded) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                } else {
                    Color.Transparent
                },
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isUploaded) {
                    "Bank Statement Uploaded ✓"
                } else {
                    "Upload documents"
                },
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun CardNumberField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Card Number",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("Enter 16-digit card number")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MerchantBorder,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun WalletNumberField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Wallet Phone Number",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("Enter mobile wallet number")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MerchantBorder,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun SettlementSubmitButton(
    enabled: Boolean,
    onSubmitClick: () -> Unit
) {
    Box(
        modifier = Modifier.padding(24.dp)
    ) {
        Button(
            onClick = onSubmitClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = "Submit",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

//package com.example.merchantapp.presentation.signup.steps
//
//import android.content.Context
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.AccountBalance
//import androidx.compose.material.icons.filled.CreditCard
//import androidx.compose.material.icons.filled.KeyboardArrowDown
//import androidx.compose.material.icons.filled.Wallet
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.FilterChip
//import androidx.compose.material3.FilterChipDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.LinearProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.OutlinedTextFieldDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextDecoration
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.example.merchantapp.presentation.signup.SettlementType
//import com.example.merchantapp.presentation.signup.SignUpEffect
//import com.example.merchantapp.presentation.signup.SignUpIntent
//import com.example.merchantapp.presentation.signup.SignUpViewModel
//import com.example.merchantapp.ui.theme.MerchantBorder
//import com.example.merchantapp.ui.theme.MerchantSecondaryText
//
//@Composable
//fun SettlementAccountScreen(
//    onNavigateBack: () -> Unit,
//    onCompleteSignUp: () -> Unit,
//    viewModel: SignUpViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsStateWithLifecycle()
//    val context = LocalContext.current
//    var isBankExpanded by remember { mutableStateOf(false) }
//    val banksList = listOf("Al Rajhi Bank", "Saudi National Bank (SNB)", "Riyad Bank", "Alinma Bank", "Arab National Bank", "Bank Albilad")
//
//    // محدد الملفات الحقيقي لاختيار ملف الـ Bank Statement
//    val filePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        if (uri != null) {
//            viewModel.onIntent(SignUpIntent.UploadBankStatementClicked)
//        }
//    }
//
//    LaunchedEffect(viewModel) {
//        viewModel.effect.collect { effect ->
//            when (effect) {
//                SignUpEffect.NavigateBack -> onNavigateBack()
//                SignUpEffect.SignUpCompleted -> onCompleteSignUp()
//                is SignUpEffect.ShowToast -> {
//                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
//                }
//                else -> {}
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .statusBarsPadding()
//            .background(MaterialTheme.colorScheme.background),
//        verticalArrangement = Arrangement.SpaceBetween
//    ) {
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .verticalScroll(rememberScrollState())
//        ) {
//            /* TOP BAR */
//            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            modifier = Modifier.clickable { viewModel.onIntent(SignUpIntent.PreviousStepClicked) }
//                        )
//                        Text(
//                            text = "Step 8 of 8",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = MerchantSecondaryText
//                        )
//                    }
//
//                    Text(
//                        text = "Save as draft",
//                        style = MaterialTheme.typography.bodyMedium.copy(
//                            color = MaterialTheme.colorScheme.primary,
//                            textDecoration = TextDecoration.Underline
//                        ),
//                        modifier = Modifier.clickable { viewModel.onIntent(SignUpIntent.SaveAsDraftClicked) }
//                    )
//                }
//            }
//
//            /* LINEAR PROGRESS INDICATOR */
//            LinearProgressIndicator(
//                progress = { 8f / 8f },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(4.dp),
//                color = MaterialTheme.colorScheme.primary,
//                trackColor = MerchantBorder.copy(alpha = 0.25f),
//                strokeCap = StrokeCap.Butt
//            )
//
//            /* CONTENT */
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(24.dp)
//            ) {
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(text = "Add Settlement Account", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(text = "Choose how you'd like to receive your payments.", style = MaterialTheme.typography.bodyMedium, color = MerchantSecondaryText)
//
//                Spacer(modifier = Modifier.height(20.dp))
//
//                /* SETTLEMENT TYPE CHIPS */
//                Row(
//                    modifier = Modifier.fillMaxWidth().height(50.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    FilterChip(
//                        selected = state.settlementType == SettlementType.BANK_ACCOUNT,
//                        onClick = { viewModel.onIntent(SignUpIntent.UpdateSettlementType(SettlementType.BANK_ACCOUNT)) },
//                        label = { Text("Bank Account", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), maxLines = 1) },
//                        leadingIcon = {
//                            Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.height(18.dp))
//                        },
//                        modifier = Modifier.weight(1f),
//                        colors = FilterChipDefaults.filterChipColors(
//                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
//                            selectedLabelColor = MaterialTheme.colorScheme.primary,
//                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary
//                        )
//                    )
//                    FilterChip(
//                        selected = state.settlementType == SettlementType.CARD,
//                        onClick = { viewModel.onIntent(SignUpIntent.UpdateSettlementType(SettlementType.CARD)) },
//                        label = { Text("Card", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), maxLines = 1) },
//                        leadingIcon = {
//                            Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.height(18.dp))
//                        },
//                        modifier = Modifier.weight(1f),
//                        colors = FilterChipDefaults.filterChipColors(
//                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
//                            selectedLabelColor = MaterialTheme.colorScheme.primary,
//                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary
//                        )
//                    )
//                    FilterChip(
//                        selected = state.settlementType == SettlementType.WALLET,
//                        onClick = { viewModel.onIntent(SignUpIntent.UpdateSettlementType(SettlementType.WALLET)) },
//                        label = { Text("Wallet", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), maxLines = 1) },
//                        leadingIcon = {
//                            Icon(imageVector = Icons.Default.Wallet, contentDescription = null, modifier = Modifier.height(18.dp))
//                        },
//                        modifier = Modifier.weight(1f),
//                        colors = FilterChipDefaults.filterChipColors(
//                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
//                            selectedLabelColor = MaterialTheme.colorScheme.primary,
//                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary
//                        )
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(20.dp))
//
//                /* DYNAMIC CONTENT */
//                when (state.settlementType) {
//                    SettlementType.BANK_ACCOUNT -> {
//                        Text(text = "Select your bank", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        Box(modifier = Modifier.fillMaxWidth()) {
//                            OutlinedTextField(
//                                value = state.selectedBank,
//                                onValueChange = {},
//                                readOnly = true,
//                                placeholder = { Text("Choose your bank") },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(52.dp)
//                                    .clickable { isBankExpanded = true },
//                                enabled = false,
//                                trailingIcon = {
//                                    Icon(
//                                        imageVector = Icons.Default.KeyboardArrowDown,
//                                        contentDescription = "Dropdown",
//                                        modifier = Modifier.clickable { isBankExpanded = true }
//                                    )
//                                },
//                                textStyle = MaterialTheme.typography.bodyLarge,
//                                colors = OutlinedTextFieldDefaults.colors(
//                                    disabledBorderColor = MerchantBorder,
//                                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
//                                    disabledPlaceholderColor = MerchantSecondaryText
//                                )
//                            )
//
//                            Box(
//                                modifier = Modifier
//                                    .matchParentSize()
//                                    .clickable { isBankExpanded = true }
//                            ) {}
//
//                            DropdownMenu(
//                                expanded = isBankExpanded,
//                                onDismissRequest = { isBankExpanded = false },
//                                modifier = Modifier
//                                    .background(MaterialTheme.colorScheme.surface)
//                                    .fillMaxWidth(0.88f)
//                            ) {
//                                banksList.forEach { bank ->
//                                    val isSelected = state.selectedBank == bank
//                                    DropdownMenuItem(
//                                        text = {
//                                            Text(
//                                                text = bank,
//                                                style = MaterialTheme.typography.bodyMedium,
//                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
//                                            )
//                                        },
//                                        onClick = {
//                                            viewModel.onIntent(SignUpIntent.UpdateBank(bank))
//                                            isBankExpanded = false
//                                        },
//                                        modifier = Modifier.background(
//                                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
//                                        )
//                                    )
//                                }
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        Text(text = "IBAN number", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        OutlinedTextField(
//                            value = state.ibanNumber,
//                            onValueChange = { input ->
//                                if (input.all { it.isDigit() }) {
//                                    viewModel.onIntent(SignUpIntent.UpdateIban(input))
//                                }
//                            },
//                            placeholder = { Text("Enter or paste IBAN number here") },
//                            modifier = Modifier.fillMaxWidth().height(52.dp),
//                            singleLine = true,
//                            textStyle = MaterialTheme.typography.bodyLarge,
//                            colors = OutlinedTextFieldDefaults.colors(
//                                unfocusedBorderColor = MerchantBorder,
//                                focusedBorderColor = MaterialTheme.colorScheme.primary
//                            )
//                        )
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        Text(text = "Bank Statement", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        OutlinedButton(
//                            onClick = { filePickerLauncher.launch("application/pdf") },
//                            modifier = Modifier.fillMaxWidth().height(52.dp),
//                            shape = RoundedCornerShape(8.dp),
//                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                containerColor = if (state.isBankStatementUploaded) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
//                                contentColor = MaterialTheme.colorScheme.primary
//                            )
//                        ) {
//                            Text(
//                                text = if (state.isBankStatementUploaded) "Bank Statement Uploaded ✓" else "Upload documents",
//                                style = MaterialTheme.typography.labelLarge
//                            )
//                        }
//                    }
//
//                    SettlementType.CARD -> {
//                        Text(text = "Card Number", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        OutlinedTextField(
//                            value = state.cardNumber,
//                            onValueChange = { input ->
//                                if (input.all { it.isDigit() }) {
//                                    viewModel.onIntent(SignUpIntent.UpdateCardNumber(input))
//                                }
//                            },
//                            placeholder = { Text("Enter 16-digit card number") },
//                            modifier = Modifier.fillMaxWidth().height(52.dp),
//                            singleLine = true,
//                            textStyle = MaterialTheme.typography.bodyLarge,
//                            colors = OutlinedTextFieldDefaults.colors(
//                                unfocusedBorderColor = MerchantBorder,
//                                focusedBorderColor = MaterialTheme.colorScheme.primary
//                            )
//                        )
//                    }
//
//                    SettlementType.WALLET -> {
//                        Text(text = "Wallet Phone Number", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        OutlinedTextField(
//                            value = state.walletNumber,
//                            onValueChange = { input ->
//                                if (input.all { it.isDigit() }) {
//                                    viewModel.onIntent(SignUpIntent.UpdateWalletNumber(input))
//                                }
//                            },
//                            placeholder = { Text("Enter mobile wallet number") },
//                            modifier = Modifier.fillMaxWidth().height(52.dp),
//                            singleLine = true,
//                            textStyle = MaterialTheme.typography.bodyLarge,
//                            colors = OutlinedTextFieldDefaults.colors(
//                                unfocusedBorderColor = MerchantBorder,
//                                focusedBorderColor = MaterialTheme.colorScheme.primary
//                            )
//                        )
//                    }
//                }
//            }
//        }
//
//        /* SUBMIT BUTTON */
//        Box(modifier = Modifier.padding(24.dp)) {
//            Button(
//                onClick = {
//                    viewModel.onIntent(SignUpIntent.NextStepClicked)
//                    onCompleteSignUp()
//                },
//                enabled = state.isNextEnabled,
//                modifier = Modifier.fillMaxWidth().height(48.dp),
//                shape = RoundedCornerShape(8.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
//                    disabledContentColor = Color.White.copy(alpha = 0.7f)
//                )
//            ) {
//                Text(text = "Submit", style = MaterialTheme.typography.labelLarge)
//            }
//        }
//    }
//}