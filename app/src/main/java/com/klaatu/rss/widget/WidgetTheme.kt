package com.klaatu.rss.widget

import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProvider as DayNightColorProvider
import androidx.glance.unit.ColorProvider

enum class WidgetThemeMode(
    val id: String,
    val displayName: String,
) {
    SYSTEM("system", "Follow Device"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark");

    companion object {
        fun fromId(id: String?): WidgetThemeMode =
            entries.firstOrNull { it.id == id } ?: SYSTEM
    }
}

enum class WidgetHeaderStyle(
    val id: String,
    val displayName: String,
) {
    BOLD("bold", "Bold"),
    TONAL("tonal", "Light Tonal");

    companion object {
        fun fromId(id: String?): WidgetHeaderStyle =
            entries.firstOrNull { it.id == id } ?: BOLD
    }
}

data class ThemePalette(
    val headerBg: Color,
    val headerTitle: Color,
    val headerSubtitle: Color,
    val cardBg: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val divider: Color,
    val placeholderBg: Color,
)

enum class WidgetThemePreset(
    val id: String,
    val displayName: String,
    val light: ThemePalette,
    val tonalLight: ThemePalette,
    val dark: ThemePalette,
) {
    MATERIAL_BLUE(
        id = "material_blue",
        displayName = "Ocean Blue",
        light = ThemePalette(
            headerBg = Color(0xFF1565C0),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFD6E6FA),
            cardBg = Color(0xFFF2F7FD),
            primaryText = Color(0xFF101C2B),
            secondaryText = Color(0xFF4A5E75),
            divider = Color(0xFFDCE6F2),
            placeholderBg = Color(0xFFE1ECF8),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFD3E3FD),
            headerTitle = Color(0xFF0B2545),
            headerSubtitle = Color(0xFF355178),
            cardBg = Color(0xFFF8FBFF),
            primaryText = Color(0xFF101C2B),
            secondaryText = Color(0xFF4A5E75),
            divider = Color(0xFFE1EAF5),
            placeholderBg = Color(0xFFE7F0FA),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF0D3B73),
            headerTitle = Color(0xFFEAF2FF),
            headerSubtitle = Color(0xFFAECBF5),
            cardBg = Color(0xFF0E1622),
            primaryText = Color(0xFFE6EEF9),
            secondaryText = Color(0xFF96ACC9),
            divider = Color(0xFF1D2B3E),
            placeholderBg = Color(0xFF172334),
        ),
    ),
    MATERIAL_TEAL(
        id = "material_teal",
        displayName = "Teal",
        light = ThemePalette(
            headerBg = Color(0xFF00695C),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFCDEBE7),
            cardBg = Color(0xFFF1F8F7),
            primaryText = Color(0xFF0E201E),
            secondaryText = Color(0xFF45635F),
            divider = Color(0xFFD7EAE7),
            placeholderBg = Color(0xFFDDF0ED),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFC9ECE6),
            headerTitle = Color(0xFF052924),
            headerSubtitle = Color(0xFF2D5953),
            cardBg = Color(0xFFF7FCFB),
            primaryText = Color(0xFF0E201E),
            secondaryText = Color(0xFF45635F),
            divider = Color(0xFFDCEEEB),
            placeholderBg = Color(0xFFE3F3F0),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF004038),
            headerTitle = Color(0xFFE5F7F4),
            headerSubtitle = Color(0xFFA3DBD3),
            cardBg = Color(0xFF0B1917),
            primaryText = Color(0xFFE2F2EF),
            secondaryText = Color(0xFF8FB8B2),
            divider = Color(0xFF18302C),
            placeholderBg = Color(0xFF132623),
        ),
    ),
    MATERIAL_INDIGO(
        id = "material_indigo",
        displayName = "Indigo",
        light = ThemePalette(
            headerBg = Color(0xFF283593),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFD5DAF5),
            cardBg = Color(0xFFF3F4FB),
            primaryText = Color(0xFF12162B),
            secondaryText = Color(0xFF4E5578),
            divider = Color(0xFFDFE2F2),
            placeholderBg = Color(0xFFE4E7F6),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFDADFF7),
            headerTitle = Color(0xFF131A45),
            headerSubtitle = Color(0xFF3F487A),
            cardBg = Color(0xFFF9FAFE),
            primaryText = Color(0xFF12162B),
            secondaryText = Color(0xFF4E5578),
            divider = Color(0xFFE4E7F5),
            placeholderBg = Color(0xFFEAECF9),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF1E2448),
            headerTitle = Color(0xFFEEF1FF),
            headerSubtitle = Color(0xFFB3BCE6),
            cardBg = Color(0xFF111424),
            primaryText = Color(0xFFE8ECFA),
            secondaryText = Color(0xFF9BA4C8),
            divider = Color(0xFF212642),
            placeholderBg = Color(0xFF1A1E36),
        ),
    ),
    MATERIAL_SAGE(
        id = "material_sage",
        displayName = "Sage",
        light = ThemePalette(
            headerBg = Color(0xFF2E7D32),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFD4EED6),
            cardBg = Color(0xFFF2F8F2),
            primaryText = Color(0xFF112113),
            secondaryText = Color(0xFF49634B),
            divider = Color(0xFFD9EAD9),
            placeholderBg = Color(0xFFE0EFE1),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFD1ECD3),
            headerTitle = Color(0xFF0C2B10),
            headerSubtitle = Color(0xFF335937),
            cardBg = Color(0xFFF8FCF8),
            primaryText = Color(0xFF112113),
            secondaryText = Color(0xFF49634B),
            divider = Color(0xFFDFEFE0),
            placeholderBg = Color(0xFFE6F3E7),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF133A2A),
            headerTitle = Color(0xFFE8F7F0),
            headerSubtitle = Color(0xFFA8D5C0),
            cardBg = Color(0xFF0B1A13),
            primaryText = Color(0xFFE2F1EA),
            secondaryText = Color(0xFF8EB3A0),
            divider = Color(0xFF193025),
            placeholderBg = Color(0xFF14271E),
        ),
    ),
    MATERIAL_TERRACOTTA(
        id = "material_terracotta",
        displayName = "Terracotta",
        light = ThemePalette(
            headerBg = Color(0xFFBF360C),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFF9D8CE),
            cardBg = Color(0xFFFDF4F1),
            primaryText = Color(0xFF2B140E),
            secondaryText = Color(0xFF734D43),
            divider = Color(0xFFF2DFD8),
            placeholderBg = Color(0xFFF6E5DF),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFF8D8CE),
            headerTitle = Color(0xFF3D1307),
            headerSubtitle = Color(0xFF753D2D),
            cardBg = Color(0xFFFFFAF8),
            primaryText = Color(0xFF2B140E),
            secondaryText = Color(0xFF734D43),
            divider = Color(0xFFF5E5DF),
            placeholderBg = Color(0xFFF9ECE7),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF5C1E0B),
            headerTitle = Color(0xFFFFEFEA),
            headerSubtitle = Color(0xFFEBB8A8),
            cardBg = Color(0xFF1C100C),
            primaryText = Color(0xFFF8EAE6),
            secondaryText = Color(0xFFC29A8E),
            divider = Color(0xFF331E18),
            placeholderBg = Color(0xFF291813),
        ),
    ),
    MATERIAL_PLUM(
        id = "material_plum",
        displayName = "Plum",
        light = ThemePalette(
            headerBg = Color(0xFF6A1B9A),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFEBD6F7),
            cardBg = Color(0xFFF8F3FB),
            primaryText = Color(0xFF21102B),
            secondaryText = Color(0xFF634873),
            divider = Color(0xFFE9DCF2),
            placeholderBg = Color(0xFFEEE3F6),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFEAD4F7),
            headerTitle = Color(0xFF2A0B3D),
            headerSubtitle = Color(0xFF5B3573),
            cardBg = Color(0xFFFCF9FE),
            primaryText = Color(0xFF21102B),
            secondaryText = Color(0xFF634873),
            divider = Color(0xFFEEE3F5),
            placeholderBg = Color(0xFFF3EBF9),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF3B1257),
            headerTitle = Color(0xFFF5ECFC),
            headerSubtitle = Color(0xFFD2B0EB),
            cardBg = Color(0xFF170E1F),
            primaryText = Color(0xFFF0E6F7),
            secondaryText = Color(0xFFB396C9),
            divider = Color(0xFF2A1B38),
            placeholderBg = Color(0xFF22152E),
        ),
    ),
    MATERIAL_AMBER(
        id = "material_amber",
        displayName = "Amber",
        light = ThemePalette(
            headerBg = Color(0xFFA65E00),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFFBE2C0),
            cardBg = Color(0xFFFDF8EE),
            primaryText = Color(0xFF281B08),
            secondaryText = Color(0xFF6E593D),
            divider = Color(0xFFF0E4CE),
            placeholderBg = Color(0xFFF5EAD6),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFF9E1BE),
            headerTitle = Color(0xFF361F02),
            headerSubtitle = Color(0xFF6E4B1D),
            cardBg = Color(0xFFFFFCF6),
            primaryText = Color(0xFF281B08),
            secondaryText = Color(0xFF6E593D),
            divider = Color(0xFFF3EAD7),
            placeholderBg = Color(0xFFF7EFE0),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF4F2E05),
            headerTitle = Color(0xFFFFF4E3),
            headerSubtitle = Color(0xFFE8C89B),
            cardBg = Color(0xFF1A140A),
            primaryText = Color(0xFFF6EFE2),
            secondaryText = Color(0xFFBFA98A),
            divider = Color(0xFF2E2414),
            placeholderBg = Color(0xFF241C0F),
        ),
    ),
    MATERIAL_ROSE(
        id = "material_rose",
        displayName = "Rose",
        light = ThemePalette(
            headerBg = Color(0xFFAD1457),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFF7D1E1),
            cardBg = Color(0xFFFDF2F6),
            primaryText = Color(0xFF2A0F1B),
            secondaryText = Color(0xFF704658),
            divider = Color(0xFFF1DCE4),
            placeholderBg = Color(0xFFF6E3EA),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFF7D2E1),
            headerTitle = Color(0xFF3B091E),
            headerSubtitle = Color(0xFF70324D),
            cardBg = Color(0xFFFFF8FA),
            primaryText = Color(0xFF2A0F1B),
            secondaryText = Color(0xFF704658),
            divider = Color(0xFFF4E2E9),
            placeholderBg = Color(0xFFF8E9EF),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF570D2E),
            headerTitle = Color(0xFFFFECF3),
            headerSubtitle = Color(0xFFE8ADC6),
            cardBg = Color(0xFF1B0D14),
            primaryText = Color(0xFFF7E6EE),
            secondaryText = Color(0xFFC291A7),
            divider = Color(0xFF331925),
            placeholderBg = Color(0xFF29141E),
        ),
    ),
    MATERIAL_SLATE(
        id = "material_slate",
        displayName = "Slate",
        light = ThemePalette(
            headerBg = Color(0xFF37474F),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFD5DFE4),
            cardBg = Color(0xFFF3F5F7),
            primaryText = Color(0xFF141B1F),
            secondaryText = Color(0xFF516068),
            divider = Color(0xFFDFE5E8),
            placeholderBg = Color(0xFFE4EAED),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFD8E1E6),
            headerTitle = Color(0xFF141D22),
            headerSubtitle = Color(0xFF42525B),
            cardBg = Color(0xFFF9FAFB),
            primaryText = Color(0xFF141B1F),
            secondaryText = Color(0xFF516068),
            divider = Color(0xFFE3E8EB),
            placeholderBg = Color(0xFFEAEFF2),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF1E262B),
            headerTitle = Color(0xFFEDF2F5),
            headerSubtitle = Color(0xFFAEBCC4),
            cardBg = Color(0xFF101417),
            primaryText = Color(0xFFE6ECEF),
            secondaryText = Color(0xFF97A6AE),
            divider = Color(0xFF20282E),
            placeholderBg = Color(0xFF181F24),
        ),
    ),
    AMOLED(
        id = "amoled_dark",
        displayName = "Monochrome",
        light = ThemePalette(
            headerBg = Color(0xFF1F2229),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFD1D5DB),
            cardBg = Color(0xFFFFFFFF),
            primaryText = Color(0xFF111317),
            secondaryText = Color(0xFF5F6368),
            divider = Color(0xFFE5E7EB),
            placeholderBg = Color(0xFFF1F3F5),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFE5E7EB),
            headerTitle = Color(0xFF111317),
            headerSubtitle = Color(0xFF4B5563),
            cardBg = Color(0xFFFFFFFF),
            primaryText = Color(0xFF111317),
            secondaryText = Color(0xFF5F6368),
            divider = Color(0xFFE5E7EB),
            placeholderBg = Color(0xFFF3F4F6),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF181A20),
            headerTitle = Color(0xFFF1F3F4),
            headerSubtitle = Color(0xFF9AA0A6),
            cardBg = Color(0xFF000000),
            primaryText = Color(0xFFECEFF1),
            secondaryText = Color(0xFF9AA0A6),
            divider = Color(0xFF1E2229),
            placeholderBg = Color(0xFF1A1D24),
        ),
    );

    fun paletteFor(isDark: Boolean, headerStyle: WidgetHeaderStyle = WidgetHeaderStyle.BOLD): ThemePalette =
        when {
            isDark -> dark
            headerStyle == WidgetHeaderStyle.TONAL -> tonalLight
            else -> light
        }

    fun colorProvider(
        mode: WidgetThemeMode,
        headerStyle: WidgetHeaderStyle,
        selector: (ThemePalette) -> Color,
    ): ColorProvider {
        val dayPalette = if (headerStyle == WidgetHeaderStyle.TONAL) tonalLight else light
        return when (mode) {
            WidgetThemeMode.SYSTEM -> DayNightColorProvider(day = selector(dayPalette), night = selector(dark))
            WidgetThemeMode.LIGHT -> ColorProvider(selector(dayPalette))
            WidgetThemeMode.DARK -> ColorProvider(selector(dark))
        }
    }

    companion object {
        fun fromId(id: String?): WidgetThemePreset = when (id) {
            "classic_blue", "material_light" -> MATERIAL_BLUE
            "forest_green" -> MATERIAL_SAGE
            "warm_sepia" -> MATERIAL_AMBER
            "slate_dark", "dark_indigo" -> MATERIAL_INDIGO
            "dark_forest" -> MATERIAL_SAGE
            else -> entries.firstOrNull { it.id == id } ?: MATERIAL_BLUE
        }
    }
}
