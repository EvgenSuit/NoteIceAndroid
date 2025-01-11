package com.suit.noteice.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    labelMedium = TextStyle(
        fontFamily = ubuntuMedium,
        fontSize = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ubuntuLight,
        fontSize = 17.sp
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