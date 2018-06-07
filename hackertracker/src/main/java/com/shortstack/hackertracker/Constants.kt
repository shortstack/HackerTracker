package com.shortstack.hackertracker

object Constants {

    const val API_URL_BASE = "https://info.defcon.org/json/"
    const val API_GITHUB_BASE = "https://rawgit.com/BeezleLabs/conferences/feature/restructure/"

    const val HOUR_IN_SECONDS = 3600

    private const val AUG_29_2017_8AM = 1501340400000L
    const val DEBUG_FORCE_TIME_DATE = AUG_29_2017_8AM

    private const val TIMER_INTERVAL_FIVE_MIN: Long = 300000
    private const val TIMER_INTERVAL_DEBUG: Long = 5000

    val TIMER_INTERVAL = if (BuildConfig.DEBUG) TIMER_INTERVAL_DEBUG else TIMER_INTERVAL_FIVE_MIN

    // TODO: Replace this with a feedback email.
    const val FEEDBACK_EMAIL = "chrisporter0111@gmail.com"
}
