package com.rank.tempbox.ui.theme

import androidx.compose.ui.graphics.Color

// Clean, Professional, Minimalist Theme

val BackgroundDark = Color(0xFF121212)
val CardDark = Color(0xFF1E1E1E)
val CardBorderDark = Color(0xFF2C2C2C)
val AccentBlue = Color(0xFF2563EB) // Royal Blue
val TextPrimaryDark = Color(0xFFF9FAFB)
val TextSecondaryDark = Color(0xFF9CA3AF)

val BackgroundLight = Color(0xFFFEFCE8) // Very light yellow background
val CardLight = Color(0xFFFFFFFF)
val CardBorderLight = Color(0xFFFEF08A) // Soft yellow border
val TextPrimaryLight = Color(0xFF422006) // Dark brown text for contrast
val TextSecondaryLight = Color(0xFFA16207) // Medium brown/yellow text

val AccentYellow = Color(0xFFEAB308) // Bright yellow accent

// Shared
val ExpiryRed = Color(0xFFEF4444)
val ExpiryOrange = Color(0xFFF59E0B)
val Success = Color(0xFF10B981)
val Error = ExpiryRed
val BadgeRed = ExpiryRed
val AvatarColors = listOf(
    Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFFEC4899),
    Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF6366F1)
)

// Dark mode palette mappings
val DarkOuterBg = Color.Black
val DarkPhoneBg = BackgroundDark
val DarkCardBg = CardDark
val DarkHeroBgStart = BackgroundDark
val DarkHeroBgEnd = BackgroundDark
val DarkText = TextPrimaryDark
val DarkSub = TextSecondaryDark
val DarkDim = Color(0xFF4B5563)
val DarkRowBorder = CardBorderDark
val DarkTipBg = Color(0xFF1E3A8A).copy(alpha = 0.3f)
val DarkTipBorder = Color(0xFF1E40AF).copy(alpha = 0.5f)
val DarkNavBg = CardDark
val DarkNavBorder = CardBorderDark
val DarkCardBorder = CardBorderDark
val DarkDomainChip = Color(0xFF374151)
val DarkDomainChipBorder = Color(0xFF4B5563)
val DarkNewBtnBg = AccentBlue.copy(alpha = 0.1f)
val DarkNewBtnBorder = AccentBlue.copy(alpha = 0.3f)
val DarkHomeIndicator = Color(0xFF4B5563)
val DarkStatusText = TextPrimaryDark
val DarkSurfaceVariant = CardDark
val AccentTeal = AccentBlue
val DarkMuted = Color(0xFF1F2937)

// Light mode palette mappings
val LightOuterBg = Color.White
val LightPhoneBg = BackgroundLight
val LightCardBg = CardLight
val LightHeroBgStart = BackgroundLight
val LightHeroBgEnd = BackgroundLight
val LightText = TextPrimaryLight
val LightSub = TextSecondaryLight
val LightDim = Color(0xFFD1D5DB)
val LightRowBorder = CardBorderLight
val LightTipBg = Color(0xFFFEF08A)
val LightTipBorder = Color(0xFFFDE047)
val LightNavBg = CardLight
val LightNavBorder = CardBorderLight
val LightCardBorder = CardBorderLight
val LightDomainChip = Color(0xFFFEF08A)
val LightDomainChipBorder = Color(0xFFFDE047)
val LightNewBtnBg = AccentYellow.copy(alpha = 0.1f)
val LightNewBtnBorder = AccentYellow.copy(alpha = 0.3f)
val LightHomeIndicator = Color(0xFFA16207)
val LightStatusText = TextPrimaryLight
val LightSurfaceVariant = CardLight
val LightAccentTeal = AccentYellow
val LightMuted = Color(0xFFFEF9C3)
