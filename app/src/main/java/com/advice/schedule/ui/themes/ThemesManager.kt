package com.advice.schedule.ui.themes

import com.advice.schedule.utilities.Storage

class ThemesManager(private val storage: Storage) {

    enum class Theme(val label: String) {
        Dark("Dark"),
        Light("Light"),
        SafeMode("Safe Mode"),
        Developer("Advice")
    }

    fun getThemes(): List<Theme> {
        val list = ArrayList<Theme>()
        list.addAll(listOf(Theme.Light, Theme.Dark))

        if (storage.getPreference(Storage.SAFE_MODE_ENABLED, false))
            list.add(Theme.SafeMode)

        if (storage.getPreference(Storage.DEVELOPER_THEME_UNLOCKED, false))
            list.add(Theme.Developer)

        return list
    }
}