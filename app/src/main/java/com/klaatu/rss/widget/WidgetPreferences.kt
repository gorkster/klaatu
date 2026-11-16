package com.klaatu.rss.widget

import android.content.Context

object WidgetPreferences {
    private const val PREFS_NAME = "klaatu_widget_prefs"
    private const val KEY_THEME = "theme_id"
    private const val KEY_CATEGORY = "selected_category"

    fun getTheme(context: Context): WidgetThemePreset {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return WidgetThemePreset.fromId(prefs.getString(KEY_THEME, null))
    }

    fun setTheme(context: Context, theme: WidgetThemePreset) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME, theme.id)
            .apply()
    }

    fun getCategory(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CATEGORY, null)?.takeIf { it.isNotBlank() }
    }

    fun setCategory(context: Context, category: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CATEGORY, category)
            .apply()
    }
}
