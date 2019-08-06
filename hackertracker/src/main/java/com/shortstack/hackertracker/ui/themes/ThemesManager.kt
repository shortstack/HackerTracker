package com.shortstack.hackertracker.ui.themes

import com.shortstack.hackertracker.BuildConfig

object ThemesManager {

    const val THEME_DARK = 1
    const val THEME_LIGHT = 2
    const val THEME_ADVICE = 3

    fun getThemes(): List<ThemeContainer> {

        val list = ArrayList<ThemeContainer>()

        list.add(ThemeContainer("Dark", THEME_DARK))
        list.add(ThemeContainer("Light", THEME_LIGHT))

        if(BuildConfig.DEBUG) {
            list.add(ThemeContainer("Advice", THEME_ADVICE))
        }


        return list
    }
}