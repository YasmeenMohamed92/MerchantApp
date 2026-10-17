package com.example.merchantapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MerchantLightColorScheme = lightColorScheme(
    primary = MerchantBlue,
    onPrimary = Color.White,


    secondary = MerchantBlueLight,
    onSecondary = MerchantBlue,

    background = MerchantBackground,
    onBackground = MerchantText,

    surface = MerchantBackground,
    onSurface = MerchantText,

    surfaceVariant = MerchantBackground,
    onSurfaceVariant = MerchantSecondaryText,

    error = MerchantError,
    onError = Color.White

)

@Composable
fun MerchantAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MerchantLightColorScheme,
        typography = MerchantTypography,
        content = content
    )
}