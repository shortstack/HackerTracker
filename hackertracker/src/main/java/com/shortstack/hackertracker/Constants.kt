package com.shortstack.hackertracker

interface Constants {
    companion object {

        const val API_URL_BASE = "https://info.defcon.org/json/"
        const val API_GITHUB_BASE = "https://rawgit.com/BeezleLabs/conferences/feature/restructure/"

        const val TIME_SECONDS_IN_HOUR = 3600

        const val DEBUG_FORCE_TIME_DATE = 1501355835000L

        private const val TIMER_INTERVAL_FIVE_MIN: Long = 300000
        private const val TIMER_INTERVAL_DEBUG: Long = 5000

        val TIMER_INTERVAL = if (BuildConfig.DEBUG) TIMER_INTERVAL_DEBUG else TIMER_INTERVAL_FIVE_MIN

        // TODO: Replace this with a feedback email.
        const val FEEDBACK_EMAIL = "chrisporter0111@gmail.com"
    }
}
