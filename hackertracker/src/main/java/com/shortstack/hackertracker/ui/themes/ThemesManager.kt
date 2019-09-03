package com.shortstack.hackertracker.ui.themes

class ThemesManager {

    enum class Theme(val label: String) {
        Dark("Dark"),
        Light("Light"),
        Developer("Advice"),
        Hacker("Hacker"),
        Gambler("Gambler"),
        Queer("Queer")
    }

    fun getThemes() = Theme.values().toList()
}