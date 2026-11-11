/**
 * Which shape to use:
 *  - [extraSmall][androidx.compose.material3.Shapes.extraSmall] (4dp): text fields, small chips.
 *  - [small][androidx.compose.material3.Shapes.small] (8dp): buttons, tags, menus.
 *  - [medium][androidx.compose.material3.Shapes.medium] (12dp): cards, search inputs.
 *  - [large][androidx.compose.material3.Shapes.large] (16dp): large cards, dialogs.
 *  - [extraLarge][androidx.compose.material3.Shapes.extraLarge] (24dp): bottom sheets.
 *  - [PillShape]: fully rounded buttons and status badges.
 */
package com.example.campsitereservationapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

/** Fully rounded ends, for pill buttons and status badges. */
val PillShape = RoundedCornerShape(percent = 50)
