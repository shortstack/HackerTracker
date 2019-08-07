package com.shortstack.hackertracker.ui.themes

import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.utilities.Storage

class ThemesManager(private val storage: Storage) {

    enum class Theme(val label: String) {
        Dark("Dark"),
        Light("Light"),
        Developer("Advice"),
        Hacker("Hacker"),
        Gambler("Gambler"),
        Queer("Queer")
    }

    fun getThemes(): List<Theme> {
        val list = ArrayList<Theme>()

        list.add(Theme.Dark)
        list.add(Theme.Light)

        if(storage.isHacker) {
            list.add(Theme.Hacker)
        }

        if(storage.isGambler) {
            list.add(Theme.Gambler)
        }
        if(storage.isQueer) {
            list.add(Theme.Queer)
        }

        if (BuildConfig.DEBUG) {
            list.add(Theme.Developer)
        }

        return list
    }
}