package com.klaatu.rss.widget

import android.content.Context

data class WidgetConfig(
    val appWidgetId: Int,
    val theme: WidgetThemePreset = WidgetThemePreset.MATERIAL_BLUE,
    val themeMode: WidgetThemeMode = WidgetThemeMode.SYSTEM,
    val headerStyle: WidgetHeaderStyle = WidgetHeaderStyle.BOLD,
    val categories: Set<String> = emptySet(),
    val feedIds: Set<Long> = emptySet(),
    val customLabel: String = "",
) {
    fun resolveSubtitle(articleCount: Int): String {
        val base = when {
            customLabel.isNotBlank() -> customLabel.trim()
            categories.isEmpty() && feedIds.isEmpty() -> "All Feeds"
            categories.isNotEmpty() && feedIds.isEmpty() -> categories.sorted().joinToString(" + ")
            categories.isEmpty() && feedIds.size == 1 -> "1 Feed"
            categories.isEmpty() -> "${feedIds.size} Feeds"
            else -> "${categories.sorted().joinToString(" + ")} + ${feedIds.size} Feeds"
        }
        return "$base ($articleCount)"
    }
}

object WidgetPreferences {
    private const val PREFS_NAME = "klaatu_widget_prefs"

    private fun themeKey(id: Int) = "theme_id_$id"
    private fun themeModeKey(id: Int) = "theme_mode_$id"
    private fun headerStyleKey(id: Int) = "header_style_$id"
    private fun categoriesKey(id: Int) = "categories_$id"
    private fun feedIdsKey(id: Int) = "feed_ids_$id"
    private fun labelKey(id: Int) = "label_$id"
    private fun refreshingKey(id: Int) = "refreshing_$id"

    fun getConfig(context: Context, appWidgetId: Int): WidgetConfig {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val theme = WidgetThemePreset.fromId(prefs.getString(themeKey(appWidgetId), null))
        val themeMode = WidgetThemeMode.fromId(prefs.getString(themeModeKey(appWidgetId), null))
        val headerStyle = WidgetHeaderStyle.fromId(prefs.getString(headerStyleKey(appWidgetId), null))
        val categories = prefs.getStringSet(categoriesKey(appWidgetId), emptySet()).orEmpty().toSet()
        val feedIds = prefs.getStringSet(feedIdsKey(appWidgetId), emptySet())
            .orEmpty()
            .mapNotNull { it.toLongOrNull() }
            .toSet()
        val label = prefs.getString(labelKey(appWidgetId), "").orEmpty()
        return WidgetConfig(
            appWidgetId = appWidgetId,
            theme = theme,
            themeMode = themeMode,
            headerStyle = headerStyle,
            categories = categories,
            feedIds = feedIds,
            customLabel = label,
        )
    }

    fun saveConfig(context: Context, config: WidgetConfig) {
        val id = config.appWidgetId
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(themeKey(id), config.theme.id)
            .putString(themeModeKey(id), config.themeMode.id)
            .putString(headerStyleKey(id), config.headerStyle.id)
            .putStringSet(categoriesKey(id), config.categories)
            .putStringSet(feedIdsKey(id), config.feedIds.map { it.toString() }.toSet())
            .putString(labelKey(id), config.customLabel.trim())
            .apply()
    }

    fun deleteConfig(context: Context, appWidgetId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(themeKey(appWidgetId))
            .remove(themeModeKey(appWidgetId))
            .remove(headerStyleKey(appWidgetId))
            .remove(categoriesKey(appWidgetId))
            .remove(feedIdsKey(appWidgetId))
            .remove(labelKey(appWidgetId))
            .remove(refreshingKey(appWidgetId))
            .apply()
    }

    fun isRefreshing(context: Context, appWidgetId: Int): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(refreshingKey(appWidgetId), false)

    fun setRefreshing(context: Context, appWidgetId: Int, refreshing: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(refreshingKey(appWidgetId), refreshing)
            .apply()
    }
}
