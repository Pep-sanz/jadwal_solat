package com.arnatech.jadwalshalat

import android.content.Context
import android.content.SharedPreferences

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "theme"

    fun setTheme(context: Context, theme: String) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(KEY_THEME, theme).apply()
    }

    fun getTheme(context: Context): String? {
        val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val theme = preferences.getString(KEY_THEME, "cyan") // Default tema: "cyan"
        return theme
    }

    fun applyTheme(context: Context) {
        val theme = getTheme(context)

        when (theme) {
            "cyan" -> context.setTheme(R.style.AppTheme_Cyan)
            "red" -> context.setTheme(R.style.AppTheme_Red)
            "green" -> context.setTheme(R.style.AppTheme_Green)
            "yellow" -> context.setTheme(R.style.AppTheme_Yellow)
            "blue" -> context.setTheme(R.style.AppTheme_Blue)
        }
    }
}
