package com.nutrix.app.core.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object NutrixColors {
    val BackgroundDark = Color(0xFF070B14)
    val BackgroundSecondary = Color(0xFF0D1424)
    val CardSurface = Color(0xFF111A2E)
    val CardSurfaceTranslucent = Color(0xCC111A2E)
    val CardBorder = Color(0xFF1E2C48)
    val CardBorderGlow = Color(0x40368BFF)

    val ElectricBlue = Color(0xFF368BFF)
    val VividViolet = Color(0xFF8B5CF6)
    val Magenta = Color(0xFFD946EF)
    val MintGreen = Color(0xFF34D399)
    val WarmAmber = Color(0xFFFBBF24)
    val CoralRed = Color(0xFFFB7185)

    val TextPrimary = Color(0xFFF4F7FF)
    val TextSecondary = Color(0xFFA6B3C9)
    val TextMuted = Color(0xFF64748B)

    // Gradients
    val PrimaryGradient = Brush.horizontalGradient(
        colors = listOf(ElectricBlue, VividViolet, Magenta)
    )

    val ActionGradient = Brush.horizontalGradient(
        colors = listOf(ElectricBlue, VividViolet)
    )

    val AccentPinkGradient = Brush.horizontalGradient(
        colors = listOf(VividViolet, Magenta)
    )

    val CardGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF162038).copy(alpha = 0.85f),
            Color(0xFF0F172A).copy(alpha = 0.95f)
        )
    )

    val AuraGlow = Brush.radialGradient(
        colors = listOf(
            VividViolet.copy(alpha = 0.25f),
            ElectricBlue.copy(alpha = 0.10f),
            Color.Transparent
        )
    )
}
