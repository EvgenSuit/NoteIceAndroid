package com.suit.noteice.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.suit.noteice.R

val funnelDisplayFamily = FontFamily(Font(R.font.funnel_display_regular))
val ubuntuLight = FontFamily(Font(R.font.ubuntu_light))
val ubuntuMedium = FontFamily(Font(R.font.ubuntu_medium))

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = funnelDisplayFamily,
        fontSize = 55.sp
    ),
    titleMedium = TextStyle(
        fontFamily = funnelDisplayFamily,
        fontSize = 25.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ubuntuMedium,
        fontSize = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ubuntuLight,
        textAlign = TextAlign.Center,
        fontSize = 17.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ubuntuMedium,
        fontSize = 15.sp,
        textAlign = TextAlign.Center
    ),
    bodyMedium = TextStyle(
        fontFamily = ubuntuMedium,
        fontSize = 21.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)