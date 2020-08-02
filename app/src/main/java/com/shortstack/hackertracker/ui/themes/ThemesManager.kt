package com.shortstack.hackertracker.ui.themes

class ThemesManager {

    enum class Theme(val label: String) {
        Dark("Dark"),
        Light("Light"),
        SafeMode("Safe Mode"),
        Developer("Advice")
    }

    fun getThemes() = Theme.values().toList()
}