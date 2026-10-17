

package com.example.merchantapp.presentation.signup.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.merchantapp.presentation.signup.SignUpIntent
import com.example.merchantapp.presentation.signup.SignUpUiState
import com.example.merchantapp.ui.theme.MerchantBorder
import com.example.merchantapp.ui.theme.MerchantSecondaryText

fun Modifier.noRippleClickable(
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember {
            MutableInteractionSource()
        },
        onClick = onClick
    )
}

@Composable
fun PersonalInfoScreen(
    state: SignUpUiState,
    onIntent: (SignUpIntent) -> Unit
) {
    var countryCodeExpanded by remember {
        mutableStateOf(false)
    }

    val trimmedName = state.fullName.trim()
    val words = if (trimmedName.isEmpty()) {
        emptyList()
    } else {
        trimmedName.split("\\s+".toRegex())
    }
    val isNameValid = words.size >= 4

    val onFullNameChange: (String) -> Unit = { input ->
        if (input.all { it.isLetter() || it.isWhitespace() }) {
            onIntent(SignUpIntent.UpdateFullName(input))
        }
    }

    val onCountryCodeChange: (String) -> Unit = { code ->
        onIntent(SignUpIntent.UpdateCountryCode(code))
    }

    val onPhoneNumberChange: (String) -> Unit = { phone ->
        onIntent(SignUpIntent.UpdatePhoneNumber(phone))
    }

    val onNationalIdChange: (String) -> Unit = { id ->
        onIntent(SignUpIntent.UpdateNationalId(id))
    }

    val onTermsChange: (Boolean) -> Unit = { accepted ->
        onIntent(SignUpIntent.UpdateTerms(accepted))
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
            PersonalInfoHeader(
                onBackClick = {
                    onIntent(SignUpIntent.PreviousStepClicked)
                }
            )

            PersonalInfoProgress()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 24.dp
                    )
            ) {
                Text(
                    text = "Enter your personal details",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Complete your personal details to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MerchantSecondaryText
                )

                Spacer(Modifier.height(24.dp))

                FullNameField(
                    value = state.fullName,
                    onValueChange = onFullNameChange,
                    isValid = isNameValid
                )

                Spacer(Modifier.height(16.dp))

                PhoneNumberField(
                    countryCode = state.countryCode,
                    countryCodeExpanded = countryCodeExpanded,
                    onCountryCodeExpandedChange = {
                        countryCodeExpanded = it
                    },
                    onCountryCodeSelected = onCountryCodeChange,
                    phoneNumber = state.phoneNumber,
                    onPhoneNumberChange = onPhoneNumberChange
                )

                Spacer(Modifier.height(16.dp))

                NationalIdField(
                    value = state.nationalId,
                    onValueChange = onNationalIdChange
                )

                Spacer(Modifier.height(16.dp))

                TermsAndConditionsRow(
                    checked = state.isTermsAccepted,
                    onCheckedChange = onTermsChange
                )
            }
        }

        NextButton(
            enabled = state.isNextEnabled,
            onClick = {
                onIntent(SignUpIntent.NextStepClicked)
            }
        )
    }
}

@Composable
private fun PersonalInfoHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 24.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.noRippleClickable(onBackClick)
        )

        Text(
            text = "Step 2 of 8",
            style = MaterialTheme.typography.bodyMedium,
            color = MerchantSecondaryText
        )
    }
}

@Composable
private fun PersonalInfoProgress() {
    LinearProgressIndicator(
        progress = { 2f / 8f },
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MerchantBorder.copy(alpha = 0.25f),
        strokeCap = StrokeCap.Butt
    )
}

@Composable
private fun FullNameField(
    value: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean
) {
    Column {
        Text(
            text = "Full Name",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = {
                Text("e.g. Ahmed Mohamed Ali Hassan")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MerchantBorder,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )

        if (!isValid) {
            Spacer(Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    modifier = Modifier.size(14.dp),
                    tint = MerchantSecondaryText
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = "Please enter the first four names of your full name.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MerchantSecondaryText
                )
            }
        }
    }
}

@Composable
private fun PhoneNumberField(
    countryCode: String,
    countryCodeExpanded: Boolean,
    onCountryCodeExpandedChange: (Boolean) -> Unit,
    onCountryCodeSelected: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Phone number",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CountryCodeSelector(
                selectedCode = countryCode,
                expanded = countryCodeExpanded,
                onExpandedChange = onCountryCodeExpandedChange,
                onCodeSelected = onCountryCodeSelected
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                placeholder = {
                    Text("Enter your number")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MerchantBorder,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun NationalIdField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = "National ID",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = {
                Text("Enter your national ID")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MerchantBorder,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun TermsAndConditionsRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp)),
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MerchantBorder,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Spacer(Modifier.width(8.dp))

        val termsText = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 13.sp
                )
            ) {
                append("I agree to Paysky's ")
            }

            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append("Terms & Conditions")
            }
        }

        Text(text = termsText)
    }
}

@Composable
private fun NextButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 24.dp
        )
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary
                    .copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = "Next",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun CountryCodeSelector(
    selectedCode: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCodeSelected: (String) -> Unit
) {
    val countryCodes = listOf(
        "+20" to "🇪🇬",
        "+966" to "🇸🇦",
        "+971" to "🇦🇪",
        "+965" to "🇰🇼",
        "+974" to "🇶🇦",
        "+973" to "🇧🇭",
        "+968" to "🇴🇲"
    )

    val selectedCountry = countryCodes.firstOrNull {
        it.first == selectedCode
    } ?: countryCodes.first()

    Box {
        Row(
            modifier = Modifier
                .width(90.dp)
                .height(52.dp)
                .border(
                    width = 1.dp,
                    color = MerchantBorder,
                    shape = RoundedCornerShape(8.dp)
                )
                .noRippleClickable {
                    onExpandedChange(true)
                }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = selectedCountry.second,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.width(5.dp))

            Text(
                text = selectedCountry.first,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            },
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surface
            )
        ) {
            countryCodes.forEach { country ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(country.second)

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = country.first,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    onClick = {
                        onCodeSelected(country.first)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

//package com.example.merchantapp.presentation.signup.steps
//
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.outlined.Info
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.composed
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.example.merchantapp.presentation.signup.SignUpEffect
//import com.example.merchantapp.presentation.signup.SignUpIntent
//import com.example.merchantapp.presentation.signup.SignUpViewModel
//import com.example.merchantapp.ui.theme.MerchantBorder
//import com.example.merchantapp.ui.theme.MerchantSecondaryText
//
//@Composable
//fun PersonalInfoScreen(
//    onNavigateBack: () -> Unit,
//    onNavigateNext: () -> Unit,
//    viewModel: SignUpViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsStateWithLifecycle()
//    val context = LocalContext.current
//
//    LaunchedEffect(viewModel) {
//        viewModel.effect.collect { effect ->
//            when (effect) {
//                SignUpEffect.NavigateBack -> onNavigateBack()
//                SignUpEffect.NavigateToNextStep -> onNavigateNext()
//                is SignUpEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
//                else -> {}
//            }
//        }
//    }
//
//    val trimmedName = state.fullName.trim()
//    val words = if (trimmedName.isEmpty()) emptyList() else trimmedName.split("\\s+".toRegex())
//    val isNameValid = words.size >= 4
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
//            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBack,
//                        contentDescription = "Back",
//                        modifier = Modifier.noRippleClickable {
//                            viewModel.onIntent(SignUpIntent.PreviousStepClicked)
//                        }
//                    )
//                    Text(
//                        text = "Step 2 of 8",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MerchantSecondaryText
//                    )
//                }
//            }
//
//            LinearProgressIndicator(
//                progress = { 2f / 8f },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(4.dp),
//                color = MaterialTheme.colorScheme.primary,
//                trackColor = MerchantBorder.copy(alpha = 0.25f),
//                strokeCap = StrokeCap.Butt
//            )
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 24.dp)
//            ) {
//                Text(
//                    text = "Enter your personal details",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.onBackground
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = "Complete your personal details to continue",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MerchantSecondaryText
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Text(
//                    text = "Full Name",
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = MaterialTheme.colorScheme.onBackground
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                OutlinedTextField(
//                    value = state.fullName,
//                    onValueChange = { input ->
//                        if (input.all { it.isLetter() || it.isWhitespace() }) {
//                            viewModel.onIntent(SignUpIntent.UpdateFullName(input))
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth().height(52.dp),
//                    placeholder = { Text(text = "e.g. Ahmed Mohamed Ali Hassan") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
//                    textStyle = MaterialTheme.typography.bodyLarge,
//                    colors = OutlinedTextFieldDefaults.colors(
//                        unfocusedBorderColor = MerchantBorder,
//                        focusedBorderColor = MaterialTheme.colorScheme.primary
//                    )
//                )
//
//                Spacer(modifier = Modifier.height(6.dp))
//
//                if (!isNameValid) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.padding(start = 2.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Outlined.Info,
//                            contentDescription = "Info",
//                            modifier = Modifier.size(14.dp),
//                            tint = MerchantSecondaryText
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = "Please enter the first four names of your full name.",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MerchantSecondaryText
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "Phone number",
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = MaterialTheme.colorScheme.onBackground
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    CountryCodeSelector(
//                        selectedCode = state.countryCode,
//                        onCodeSelected = { viewModel.onIntent(SignUpIntent.UpdateCountryCode(it)) }
//                    )
//
//                    OutlinedTextField(
//                        value = state.phoneNumber,
//                        onValueChange = { viewModel.onIntent(SignUpIntent.UpdatePhoneNumber(it)) },
//                        modifier = Modifier.weight(1f).height(52.dp),
//                        placeholder = { Text(text = "Enter your number") },
//                        singleLine = true,
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                        textStyle = MaterialTheme.typography.bodyLarge,
//                        colors = OutlinedTextFieldDefaults.colors(
//                            unfocusedBorderColor = MerchantBorder,
//                            focusedBorderColor = MaterialTheme.colorScheme.primary
//                        )
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "National ID",
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = MaterialTheme.colorScheme.onBackground
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                OutlinedTextField(
//                    value = state.nationalId,
//                    onValueChange = { viewModel.onIntent(SignUpIntent.UpdateNationalId(it)) },
//                    modifier = Modifier.fillMaxWidth().height(52.dp),
//                    placeholder = { Text(text = "Enter your national ID") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                    textStyle = MaterialTheme.typography.bodyLarge,
//                    colors = OutlinedTextFieldDefaults.colors(
//                        unfocusedBorderColor = MerchantBorder,
//                        focusedBorderColor = MaterialTheme.colorScheme.primary
//                    )
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Checkbox(
//                        checked = state.isTermsAccepted,
//                        onCheckedChange = { viewModel.onIntent(SignUpIntent.UpdateTerms(it)) },
//                        modifier = Modifier.size(30.dp).clip(RoundedCornerShape(8.dp)),
//                        colors = CheckboxDefaults.colors(
//                            checkedColor = MaterialTheme.colorScheme.primary,
//                            uncheckedColor = MerchantBorder,
//                            checkmarkColor = MaterialTheme.colorScheme.onPrimary
//                        )
//                    )
//
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    val termsText = buildAnnotatedString {
//                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)) {
//                            append("I agree to Paysky's ")
//                        }
//                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)) {
//                            append("Terms & Conditions")
//                        }
//                    }
//
//                    Text(text = termsText)
//                }
//            }
//        }
//
//        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
//            Button(
//                onClick = {
//                    viewModel.onIntent(SignUpIntent.NextStepClicked)
//                    onNavigateNext()
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
//                Text(text = "Next", style = MaterialTheme.typography.labelLarge)
//            }
//        }
//    }
//}
//
//@Composable
//private fun CountryCodeSelector(
//    selectedCode: String,
//    onCodeSelected: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//    data class CountryCode(val code: String, val flag: String)
//    val countryCodes = listOf(
//        CountryCode("+20", "🇪🇬"),
//        CountryCode("+966", "🇸🇦"),
//        CountryCode("+971", "🇦🇪"),
//        CountryCode("+965", "🇰🇼"),
//        CountryCode("+974", "🇶🇦"),
//        CountryCode("+973", "🇧🇭"),
//        CountryCode("+968", "🇴🇲")
//    )
//    val selectedCountry = countryCodes.firstOrNull { it.code == selectedCode } ?: countryCodes.first()
//
//    Box {
//        Row(
//            modifier = Modifier
//                .width(90.dp)
//                .height(52.dp)
//                .border(width = 1.dp, color = MerchantBorder, shape = RoundedCornerShape(8.dp))
//                .noRippleClickable { expanded = true }
//                .padding(horizontal = 8.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text(text = selectedCountry.flag, style = MaterialTheme.typography.bodyMedium)
//            Spacer(modifier = Modifier.width(5.dp))
//            Text(text = selectedCountry.code, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
//        }
//
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
//        ) {
//            countryCodes.forEach { country ->
//                DropdownMenuItem(
//                    text = {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Text(text = country.flag, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(text = country.code, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
//                        }
//                    },
//                    onClick = {
//                        onCodeSelected(country.code)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}
//
//// Helper extension function لو مش متعريّفة برة
//fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
//    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
//}