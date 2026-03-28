package com.dayun.mlkitfacecount.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    // Small components: chips, text fields, snackbars
    small = RoundedCornerShape(8.dp),
    // Medium components: cards, dialogs
    medium = RoundedCornerShape(16.dp),
    // Large components: bottom sheets, drawers, image cards
    large = RoundedCornerShape(24.dp),
    // Extra large: full-bleed sheets
    extraLarge = RoundedCornerShape(28.dp),
)
