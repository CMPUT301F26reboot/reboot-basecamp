/**
 * The app's colors. Screens should use `MaterialTheme.colorScheme` (or the named colors at the
 * bottom of this file) instead of writing `Color(0x...)` themselves, so every screen matches.
 *
 * The palette comes from the colors the screens already used: the forest green from sign up and
 * profile, the gray text and border shades, and the green, amber and red status colors.
 *
 * Outstanding issues:
 *  - Most screens still hardcode their own colors and need to be moved onto these.
 */
package com.example.campsitereservationapp.ui.theme

import androidx.compose.ui.graphics.Color

// Forest green, the brand color.
internal val Forest10 = Color(0xFF0A1F16)
internal val Forest30 = Color(0xFF1A3A2C)
internal val Forest80 = Color(0xFFA7D3B9)
internal val Forest90 = Color(0xFFCDEFD3)

// Emerald, for secondary actions.
internal val Emerald10 = Color(0xFF064E3B)
internal val Emerald40 = Color(0xFF047857)
internal val Emerald90 = Color(0xFFD1FAE5)

// Amber, for highlights.
internal val Amber10 = Color(0xFF78350F)
internal val Amber40 = Color(0xFFB45309)
internal val Amber90 = Color(0xFFFEF3C7)

// Red, for errors.
internal val Red10 = Color(0xFF7F1D1D)
internal val Red40 = Color(0xFFB91C1C)
internal val Red90 = Color(0xFFFEE2E2)

// Neutral grays for text, borders and surfaces.
internal val Gray0 = Color(0xFFFFFFFF)
internal val Gray50 = Color(0xFFF9FAFB)
internal val Gray100 = Color(0xFFF3F4F6)
internal val Gray150 = Color(0xFFEEF0F2)
internal val Gray200 = Color(0xFFE5E7EB)
internal val Gray500 = Color(0xFF6B7280)
internal val Gray600 = Color(0xFF757575)
internal val Gray800 = Color(0xFF1F2937)
internal val Gray900 = Color(0xFF111827)

val DeleteRed = Color(0xFFEF4444)

val RatingAmber = Color(0xFFF59E0B)
