package com.dayun.mlkitfacecount.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary: Blue-Indigo (AI / vision feel) ──────────────────────────────────
val Blue80 = Color(0xFFAAC8FF)       // light-theme primary container / dark primary
val BlueGrey80 = Color(0xFFBEC6DC)   // light secondary container / dark secondary
val Cyan80 = Color(0xFF86D3E0)       // light tertiary container / dark tertiary

val Blue40 = Color(0xFF1A5CB8)       // light primary
val BlueGrey40 = Color(0xFF4A5568)   // light secondary
val Cyan40 = Color(0xFF007A8A)       // light tertiary

// ── Bounding-box accent (used directly in Canvas, not from colorScheme) ───────
// Semi-opaque teal that reads well over any photo background
val BoundingBoxStroke = Color(0xFF00E5D0)       // vivid teal outline
val BoundingBoxFill = Color(0x3300E5D0)         // 20 % opaque fill
val BoundingBoxLabelBg = Color(0xCC003D40)      // near-opaque dark teal chip bg
val BoundingBoxLabelText = Color(0xFFE0FFFD)    // on-chip text
