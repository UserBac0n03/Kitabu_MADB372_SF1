package com.stadiolinks.kitabu.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.stadiolinks.kitabu.R

/** Lora (serif) for titles and headings. Looks more like a book. */
val Lora = FontFamily(
    Font(R.font.lora_semibold, FontWeight.SemiBold),
    Font(R.font.lora_bold, FontWeight.Bold)
)

/** Inter for body text, labels and buttons. Still readable when it's small. */
val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

// Material defaults, just swapping the font family/weight below
private val Base = Typography()

/**
 * Lora for names of things (screens, schools, book titles).
 * Inter for everything else (descriptions, dates, badges, buttons).
 */
val Typography = Typography(
    displayLarge = Base.displayLarge.copy(fontFamily = Lora, fontWeight = FontWeight.Bold),
    displayMedium = Base.displayMedium.copy(fontFamily = Lora, fontWeight = FontWeight.Bold),
    displaySmall = Base.displaySmall.copy(fontFamily = Lora, fontWeight = FontWeight.Bold),
    headlineLarge = Base.headlineLarge.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    headlineMedium = Base.headlineMedium.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    headlineSmall = Base.headlineSmall.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    titleLarge = Base.titleLarge.copy(fontFamily = Lora, fontWeight = FontWeight.Bold),
    titleMedium = Base.titleMedium.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    titleSmall = Base.titleSmall.copy(fontFamily = Inter, fontWeight = FontWeight.SemiBold),
    bodyLarge = Base.bodyLarge.copy(fontFamily = Inter),
    bodyMedium = Base.bodyMedium.copy(fontFamily = Inter),
    bodySmall = Base.bodySmall.copy(fontFamily = Inter),
    labelLarge = Base.labelLarge.copy(fontFamily = Inter, fontWeight = FontWeight.SemiBold),
    labelMedium = Base.labelMedium.copy(fontFamily = Inter, fontWeight = FontWeight.Medium),
    labelSmall = Base.labelSmall.copy(fontFamily = Inter, fontWeight = FontWeight.Medium)
)
