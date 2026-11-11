
package com.example.merchantapp.presentation.login

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.merchantapp.R
import com.example.merchantapp.core.localization.LanguageManager
import com.example.merchantapp.domain.model.LoginError
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
fun LoginScreen(
    onNavigateToHome: (String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToHome -> {
                    onNavigateToHome(effect.merchantId)
                }

                LoginEffect.NavigateToSignUp -> {
                    onNavigateToSignUp()
                }
            }
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.screenWidthDp > configuration.screenHeightDp

    val isLoading = state.status is LoginStatus.Loading
    val errorStatus = state.status as? LoginStatus.Error

    if (errorStatus != null) {
        LoginErrorDialog(
            message = stringResource(
                id = errorStatus.error.toStringRes()
            ),
            onTryAgain = {
                viewModel.onIntent(LoginIntent.ErrorDismissed)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = 16.dp,
                vertical = 24.dp
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                LanguageSelector(
                    selectedLanguage = state.selectedLanguage,
                    onLanguageSelected = { language ->
                        viewModel.onIntent(
                            LoginIntent.LanguageChanged(language)
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(
                        if (isLandscape) 16.dp else 24.dp
                    )
                )

                LoginHeader()

                Spacer(
                    modifier = Modifier.height(
                        if (isLandscape) 14.dp else 20.dp
                    )
                )

                MerchantIdField(
                    value = state.merchantId,
                    onValueChange = { value ->
                        viewModel.onIntent(
                            LoginIntent.MerchantIdChanged(value)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PinInput(
                    pin = state.pin,
                    onPinChange = { value ->
                        viewModel.onIntent(
                            LoginIntent.PinChanged(value)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                RememberMeRow(
                    checked = state.rememberMe,
                    onCheckedChange = { checked ->
                        viewModel.onIntent(
                            LoginIntent.RememberMeChanged(checked)
                        )
                    },
                    onForgotPinClick = {
                        viewModel.onIntent(
                            LoginIntent.ForgotPinClicked
                        )
                    }
                )

                Spacer(modifier = Modifier.height(50.dp))

                ContinueAndFaceVerificationRow(
                    enabled = state.isContinueEnabled,
                    loading = isLoading,
                    onContinueClick = {
                        viewModel.onIntent(
                            LoginIntent.ContinueClicked
                        )
                    },
                    onFaceVerificationClick = {
                        viewModel.onIntent(
                            LoginIntent.FaceVerificationClicked
                        )
                    }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ) {
                OrLoginWithDivider()

                Spacer(modifier = Modifier.height(20.dp))

                MobileNumberButton(
                    onClick = {
                        viewModel.onIntent(
                            LoginIntent.MobileNumberClicked
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SignUpFooter(
                    onSignUpClick = {
                        viewModel.onIntent(
                            LoginIntent.SignUpClicked
                        )
                    }
                )
            }
        }
    }
}

/* ---------------------------------------------------------
   LOGIN ERROR LOCALIZATION
   --------------------------------------------------------- */

@StringRes
private fun LoginError.toStringRes(): Int {
    return when (this) {
        LoginError.INVALID_CREDENTIALS ->
            R.string.login_error_invalid_credentials

        LoginError.UNABLE_TO_LOGIN ->
            R.string.login_error_unable_to_login
    }
}

/* ---------------------------------------------------------
   LANGUAGE SELECTOR
   --------------------------------------------------------- */

@Composable
fun LanguageSelector(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier
                .noRippleClickable {
                    expanded = !expanded
                }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌐",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = selectedLanguage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "⌄",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surface
            )
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.language_english),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    onLanguageSelected("English")
                    LanguageManager.changeLanguage("en")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.language_arabic),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    onLanguageSelected("العربية")
                    LanguageManager.changeLanguage("ar")
                    expanded = false
                }
            )
        }
    }
}

/* ---------------------------------------------------------
   HEADER
   --------------------------------------------------------- */

@Composable
fun LoginHeader() {
    Column {
        Text(
            text = stringResource(R.string.login_welcome),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MerchantSecondaryText
        )
    }
}

/* ---------------------------------------------------------
   MERCHANT ID
   --------------------------------------------------------- */

@Composable
fun MerchantIdField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.merchant_id),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.merchant_id_hint),
                    style = MaterialTheme.typography.bodyLarge
                )
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

/* ---------------------------------------------------------
   PIN INPUT
   --------------------------------------------------------- */

@Composable
fun PinInput(
    pin: String,
    onPinChange: (String) -> Unit
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.pin),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    PinBox(
                        value = pin.getOrNull(index)?.toString() ?: "",
                        isActive = index == pin.length,
                        isPinFocused = isFocused,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            focusRequester.requestFocus()
                        }
                    )
                }
            }

            BasicTextField(
                value = pin,
                onValueChange = { value ->
                    if (
                        value.length <= 6 &&
                        value.all { it.isDigit() }
                    ) {
                        onPinChange(value)
                    }
                },
                modifier = Modifier
                    .size(1.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                cursorBrush = SolidColor(Color.Transparent),
                textStyle = TextStyle(
                    color = Color.Transparent,
                    fontSize = 1.sp
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.size(1.dp)
                    ) {
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun PinBox(
    value: String,
    isActive: Boolean,
    isPinFocused: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isFilled = value.isNotEmpty()

    val borderColor =
        if (isPinFocused && (isFilled || isActive)) {
            MaterialTheme.colorScheme.primary
        } else {
            MerchantBorder
        }

    val borderWidth =
        if (isPinFocused && (isFilled || isActive)) {
            2.dp
        } else {
            1.dp
        }

    Box(
        modifier = modifier
            .height(52.dp)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .noRippleClickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (isFilled) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = if (isPinFocused) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground
                }
            )
        } else if (isActive && isPinFocused) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

/* ---------------------------------------------------------
   REMEMBER ME
   --------------------------------------------------------- */

@Composable
fun RememberMeRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onForgotPinClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
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

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = stringResource(R.string.remember_me),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(R.string.forgot_pin),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.noRippleClickable {
                onForgotPinClick()
            }
        )
    }
}

/* ---------------------------------------------------------
   CONTINUE BUTTON
   --------------------------------------------------------- */

@Composable
fun ContinueAndFaceVerificationRow(
    enabled: Boolean,
    loading: Boolean,
    onContinueClick: () -> Unit,
    onFaceVerificationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onContinueClick,
            enabled = enabled && !loading,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.continue_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Image(
            painter = painterResource(
                id = R.drawable.faceverification
            ),
            contentDescription = stringResource(
                R.string.face_verification
            ),
            modifier = Modifier
                .size(48.dp)
                .noRippleClickable {
                    onFaceVerificationClick()
                }
        )
    }
}

/* ---------------------------------------------------------
   OR DIVIDER
   --------------------------------------------------------- */

@Composable
fun OrLoginWithDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f)
        )

        Text(
            text = stringResource(R.string.or_login_with),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f)
        )
    }
}

/* ---------------------------------------------------------
   MOBILE NUMBER
   --------------------------------------------------------- */

@Composable
fun MobileNumberButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = stringResource(R.string.mobile_number),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/* ---------------------------------------------------------
   SIGN UP
   --------------------------------------------------------- */

@Composable
fun SignUpFooter(
    onSignUpClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_account),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.sign_up_now),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.noRippleClickable {
                onSignUpClick()
            }
        )
    }
}

/* ---------------------------------------------------------
   ERROR DIALOG
   --------------------------------------------------------- */

@Composable
fun LoginErrorDialog(
    message: String,
    onTryAgain: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onTryAgain,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.error
                    ),
                    contentDescription = stringResource(
                        R.string.login_warning
                    ),
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = stringResource(R.string.login_unable),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MerchantSecondaryText
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onTryAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.try_again),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}
