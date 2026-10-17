package com.example.merchantapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MerchantTypography = Typography(

    // 20sp Bold
    titleLarge = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    ),

    // 16sp Bold
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    ),

    // 14sp Regular
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
    ),

    // 12sp Regular
    bodyMedium = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal
    ),

    // 11sp Regular
    bodySmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal
    ),

    // 14sp Medium
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    ),

    // 10sp Regular
    labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal
    )
)

