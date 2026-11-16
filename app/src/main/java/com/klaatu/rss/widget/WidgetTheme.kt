package com.klaatu.rss.widget

import androidx.compose.ui.graphics.Color

enum class WidgetThemePreset(
    val id: String,
    val displayName: String,
    val headerBg: Color,
    val headerTitle: Color,
    val headerSubtitle: Color,
    val cardBg: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val divider: Color,
    val placeholderBg: Color,
) {
    CLASSIC_BLUE(
        id = "classic_blue",
        displayName = "Classic Blue",
        headerBg = Color(0xFF39A7F0),
        headerTitle = Color(0xFFFFFFFF),
        headerSubtitle = Color(0xFFE3F2FD),
        cardBg = Color(0xFFFFFFFF),
        primaryText = Color(0xFF1A1C1E),
        secondaryText = Color(0xFF5F6368),
        divider = Color(0xFFE6E8EB),
        placeholderBg = Color(0xFFE1E5EA),
    ),
    AMOLED_DARK(
        id = "amoled_dark",
        displayName = "AMOLED Dark",
        headerBg = Color(0xFF16181D),
        headerTitle = Color(0xFFF1F3F4),
        headerSubtitle = Color(0xFF9AA0A6),
        cardBg = Color(0xFF000000),
        primaryText = Color(0xFFECEFF1),
        secondaryText = Color(0xFF9AA0A6),
        divider = Color(0xFF1E2229),
        placeholderBg = Color(0xFF1A1D24),
    ),
    SLATE_DARK(
        id = "slate_dark",
        displayName = "Slate Indigo",
        headerBg = Color(0xFF3F51B5),
        headerTitle = Color(0xFFFFFFFF),
        headerSubtitle = Color(0xFFC5CAE9),
        cardBg = Color(0xFF1C1F26),
        primaryText = Color(0xFFF3F4F6),
        secondaryText = Color(0xFFA0A7B4),
        divider = Color(0xFF2B303B),
        placeholderBg = Color(0xFF2B303B),
    ),
    MATERIAL_LIGHT(
        id = "material_light",
        displayName = "Modern Light",
        headerBg = Color(0xFFE8EEF5),
        headerTitle = Color(0xFF1B2430),
        headerSubtitle = Color(0xFF4B5563),
        cardBg = Color(0xFFFAFBFD),
        primaryText = Color(0xFF111827),
        secondaryText = Color(0xFF6B7280),
        divider = Color(0xFFE5E7EB),
        placeholderBg = Color(0xFFE5E9F0),
    ),
    FOREST_GREEN(
        id = "forest_green",
        displayName = "Forest",
        headerBg = Color(0xFF1B6B4A),
        headerTitle = Color(0xFFFFFFFF),
        headerSubtitle = Color(0xFFC8E6C9),
        cardBg = Color(0xFFFFFFFF),
        primaryText = Color(0xFF1A1C1E),
        secondaryText = Color(0xFF5F6368),
        divider = Color(0xFFE6EBE8),
        placeholderBg = Color(0xFFE0E8E3),
    ),
    WARM_SEPIA(
        id = "warm_sepia",
        displayName = "Warm Sepia",
        headerBg = Color(0xFF8D4E2A),
        headerTitle = Color(0xFFFFF8F0),
        headerSubtitle = Color(0xFFF5D8C5),
        cardBg = Color(0xFFFDF8F2),
        primaryText = Color(0xFF2C1E16),
        secondaryText = Color(0xFF7A6355),
        divider = Color(0xFFEDE2D6),
        placeholderBg = Color(0xFFEFE4D8),
    );

    companion object {
        fun fromId(id: String?): WidgetThemePreset =
            entries.firstOrNull { it.id == id } ?: CLASSIC_BLUE
    }
}
