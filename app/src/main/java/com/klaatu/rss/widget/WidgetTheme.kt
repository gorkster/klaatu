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
    BOLD("bold", "Vibrant"),
    TONAL("tonal", "Soft Pastel");

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
    BABY_BLUE(
        id = "material_blue",
        displayName = "Baby Blue",
        light = ThemePalette(
            headerBg = Color(0xFF4FC3F7),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFE8F7FF),
            cardBg = Color(0xFFF5FAFF),
            primaryText = Color(0xFF101C2B),
            secondaryText = Color(0xFF4A5E75),
            divider = Color(0xFFDCEAF7),
            placeholderBg = Color(0xFFE3F1FC),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFB3E5FC),
            headerTitle = Color(0xFF072B42),
            headerSubtitle = Color(0xFF2B5B7A),
            cardBg = Color(0xFFF8FCFF),
            primaryText = Color(0xFF101C2B),
            secondaryText = Color(0xFF4A5E75),
            divider = Color(0xFFE1EFFA),
            placeholderBg = Color(0xFFE8F4FD),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF0E3F66),
            headerTitle = Color(0xFFEAF6FF),
            headerSubtitle = Color(0xFFA8D8F7),
            cardBg = Color(0xFF0D1621),
            primaryText = Color(0xFFE6F0FA),
            secondaryText = Color(0xFF93AFC9),
            divider = Color(0xFF1B2B3D),
            placeholderBg = Color(0xFF152333),
        ),
    ),
    LIGHT_ORANGE(
        id = "material_amber",
        displayName = "Light Orange",
        light = ThemePalette(
            headerBg = Color(0xFFFFA726),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFFFF3E0),
            cardBg = Color(0xFFFFF9F2),
            primaryText = Color(0xFF281908),
            secondaryText = Color(0xFF6E573D),
            divider = Color(0xFFF5E5D0),
            placeholderBg = Color(0xFFF9ECDC),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFFFE0B2),
            headerTitle = Color(0xFF3D2200),
            headerSubtitle = Color(0xFF754D19),
            cardBg = Color(0xFFFFFBF6),
            primaryText = Color(0xFF281908),
            secondaryText = Color(0xFF6E573D),
            divider = Color(0xFFF6E9D8),
            placeholderBg = Color(0xFFFAF0E2),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF573407),
            headerTitle = Color(0xFFFFF4E3),
            headerSubtitle = Color(0xFFF5C98E),
            cardBg = Color(0xFF1A130A),
            primaryText = Color(0xFFF6EFE2),
            secondaryText = Color(0xFFC2A98A),
            divider = Color(0xFF302314),
            placeholderBg = Color(0xFF261B0F),
        ),
    ),
    YELLOW(
        id = "material_yellow",
        displayName = "Yellow",
        light = ThemePalette(
            headerBg = Color(0xFFFFD54F),
            headerTitle = Color(0xFF2E2200),
            headerSubtitle = Color(0xFF5C4708),
            cardBg = Color(0xFFFFFDF4),
            primaryText = Color(0xFF262008),
            secondaryText = Color(0xFF6B5F38),
            divider = Color(0xFFF2EBC9),
            placeholderBg = Color(0xFFF7F1D7),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFFFF0B3),
            headerTitle = Color(0xFF332600),
            headerSubtitle = Color(0xFF665114),
            cardBg = Color(0xFFFFFEF8),
            primaryText = Color(0xFF262008),
            secondaryText = Color(0xFF6B5F38),
            divider = Color(0xFFF4EED4),
            placeholderBg = Color(0xFFF9F4E0),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF4A3D08),
            headerTitle = Color(0xFFFFF8DB),
            headerSubtitle = Color(0xFFE6D48A),
            cardBg = Color(0xFF17150A),
            primaryText = Color(0xFFF5F1DF),
            secondaryText = Color(0xFFBCB388),
            divider = Color(0xFF2B2714),
            placeholderBg = Color(0xFF211E0F),
        ),
    ),
    PURPLE(
        id = "material_plum",
        displayName = "Purple",
        light = ThemePalette(
            headerBg = Color(0xFFBA68C8),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFF8E8FB),
            cardBg = Color(0xFFFAF5FC),
            primaryText = Color(0xFF22112B),
            secondaryText = Color(0xFF634873),
            divider = Color(0xFFECE0F2),
            placeholderBg = Color(0xFFF2E7F7),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFE1BEE7),
            headerTitle = Color(0xFF2D0A38),
            headerSubtitle = Color(0xFF5F306E),
            cardBg = Color(0xFFFCF9FE),
            primaryText = Color(0xFF22112B),
            secondaryText = Color(0xFF634873),
            divider = Color(0xFFF0E6F5),
            placeholderBg = Color(0xFFF5EEF9),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF441952),
            headerTitle = Color(0xFFF7ECFC),
            headerSubtitle = Color(0xFFD9ADE8),
            cardBg = Color(0xFF170E1C),
            primaryText = Color(0xFFF0E6F7),
            secondaryText = Color(0xFFB596C9),
            divider = Color(0xFF2B1C36),
            placeholderBg = Color(0xFF22162B),
        ),
    ),
    PINK(
        id = "material_rose",
        displayName = "Pink",
        light = ThemePalette(
            headerBg = Color(0xFFF06292),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFFFE6EE),
            cardBg = Color(0xFFFFF5F8),
            primaryText = Color(0xFF2B101A),
            secondaryText = Color(0xFF704657),
            divider = Color(0xFFF5DFEA),
            placeholderBg = Color(0xFFF9E7F0),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFF8BBD0),
            headerTitle = Color(0xFF3B091D),
            headerSubtitle = Color(0xFF702E4A),
            cardBg = Color(0xFFFFF9FB),
            primaryText = Color(0xFF2B101A),
            secondaryText = Color(0xFF704657),
            divider = Color(0xFFF6E4EC),
            placeholderBg = Color(0xFFFAECF2),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF591431),
            headerTitle = Color(0xFFFFECF3),
            headerSubtitle = Color(0xFFF0ACC6),
            cardBg = Color(0xFF1B0D14),
            primaryText = Color(0xFFF7E6EE),
            secondaryText = Color(0xFFC491A7),
            divider = Color(0xFF331A26),
            placeholderBg = Color(0xFF29141E),
        ),
    ),
    RED(
        id = "material_terracotta",
        displayName = "Coral Red",
        light = ThemePalette(
            headerBg = Color(0xFFEF5350),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFFFE6E5),
            cardBg = Color(0xFFFFF6F5),
            primaryText = Color(0xFF2B1212),
            secondaryText = Color(0xFF734848),
            divider = Color(0xFFF5DFDF),
            placeholderBg = Color(0xFFF9E7E7),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFFFCDD2),
            headerTitle = Color(0xFF3D0B0B),
            headerSubtitle = Color(0xFF753030),
            cardBg = Color(0xFFFFFAFA),
            primaryText = Color(0xFF2B1212),
            secondaryText = Color(0xFF734848),
            divider = Color(0xFFF6E4E4),
            placeholderBg = Color(0xFFFAECEC),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF5C1818),
            headerTitle = Color(0xFFFFECEC),
            headerSubtitle = Color(0xFFF0AEAE),
            cardBg = Color(0xFF1C0E0E),
            primaryText = Color(0xFFF8E7E7),
            secondaryText = Color(0xFFC49393),
            divider = Color(0xFF331B1B),
            placeholderBg = Color(0xFF291515),
        ),
    ),
    GREEN(
        id = "material_sage",
        displayName = "Green",
        light = ThemePalette(
            headerBg = Color(0xFF66BB6A),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFE8F7E9),
            cardBg = Color(0xFFF4FBF4),
            primaryText = Color(0xFF112113),
            secondaryText = Color(0xFF49634B),
            divider = Color(0xFFDCECDC),
            placeholderBg = Color(0xFFE4F2E4),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFC8E6C9),
            headerTitle = Color(0xFF0B2B0E),
            headerSubtitle = Color(0xFF2F5933),
            cardBg = Color(0xFFF8FCF8),
            primaryText = Color(0xFF112113),
            secondaryText = Color(0xFF49634B),
            divider = Color(0xFFE2F0E2),
            placeholderBg = Color(0xFFE9F5E9),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF184222),
            headerTitle = Color(0xFFE8F7EB),
            headerSubtitle = Color(0xFFA8D9B3),
            cardBg = Color(0xFF0C1A0F),
            primaryText = Color(0xFFE2F1E5),
            secondaryText = Color(0xFF8EB596),
            divider = Color(0xFF1A301F),
            placeholderBg = Color(0xFF142718),
        ),
    ),
    MINT_TEAL(
        id = "material_teal",
        displayName = "Mint Teal",
        light = ThemePalette(
            headerBg = Color(0xFF4DB6AC),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFE3F7F5),
            cardBg = Color(0xFFF3FBF9),
            primaryText = Color(0xFF0E201E),
            secondaryText = Color(0xFF45635F),
            divider = Color(0xFFD9ECE9),
            placeholderBg = Color(0xFFE1F2F0),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFB2DFDB),
            headerTitle = Color(0xFF062925),
            headerSubtitle = Color(0xFF2B5954),
            cardBg = Color(0xFFF8FCFB),
            primaryText = Color(0xFF0E201E),
            secondaryText = Color(0xFF45635F),
            divider = Color(0xFFDFEFED),
            placeholderBg = Color(0xFFE6F4F2),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF0D423C),
            headerTitle = Color(0xFFE5F7F5),
            headerSubtitle = Color(0xFFA3DBD5),
            cardBg = Color(0xFF0B1917),
            primaryText = Color(0xFFE2F2F0),
            secondaryText = Color(0xFF8FB8B3),
            divider = Color(0xFF18302D),
            placeholderBg = Color(0xFF132624),
        ),
    ),
    PERIWINKLE(
        id = "material_indigo",
        displayName = "Periwinkle",
        light = ThemePalette(
            headerBg = Color(0xFF7986CB),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFEBEEFA),
            cardBg = Color(0xFFF5F6FC),
            primaryText = Color(0xFF12162B),
            secondaryText = Color(0xFF4E5578),
            divider = Color(0xFFE0E3F4),
            placeholderBg = Color(0xFFE7E9F7),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFC5CAE9),
            headerTitle = Color(0xFF131942),
            headerSubtitle = Color(0xFF3E4675),
            cardBg = Color(0xFFF9FAFE),
            primaryText = Color(0xFF12162B),
            secondaryText = Color(0xFF4E5578),
            divider = Color(0xFFE5E8F5),
            placeholderBg = Color(0xFFECEEF9),
        ),
        dark = ThemePalette(
            headerBg = Color(0xFF242B59),
            headerTitle = Color(0xFFEEF1FF),
            headerSubtitle = Color(0xFFB5BFE8),
            cardBg = Color(0xFF101324),
            primaryText = Color(0xFFE8ECFA),
            secondaryText = Color(0xFF9BA4C8),
            divider = Color(0xFF212642),
            placeholderBg = Color(0xFF1A1E36),
        ),
    ),
    MONOCHROME(
        id = "amoled_dark",
        displayName = "Monochrome",
        light = ThemePalette(
            headerBg = Color(0xFF455A64),
            headerTitle = Color(0xFFFFFFFF),
            headerSubtitle = Color(0xFFECEFF1),
            cardBg = Color(0xFFFFFFFF),
            primaryText = Color(0xFF111317),
            secondaryText = Color(0xFF5F6368),
            divider = Color(0xFFE5E7EB),
            placeholderBg = Color(0xFFF1F3F5),
        ),
        tonalLight = ThemePalette(
            headerBg = Color(0xFFE2E6EA),
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
            "classic_blue", "material_light" -> BABY_BLUE
            "forest_green" -> GREEN
            "warm_sepia" -> LIGHT_ORANGE
            "slate_dark", "dark_indigo", "material_slate" -> PERIWINKLE
            "dark_forest" -> GREEN
            else -> entries.firstOrNull { it.id == id } ?: BABY_BLUE
        }
    }
}
