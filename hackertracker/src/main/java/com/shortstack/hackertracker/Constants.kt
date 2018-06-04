package com.shortstack.hackertracker

interface Constants {
    companion object {

        val DEFCON_DATABASE_NAME = "DEFCON25"
        val TOORCON_DATABASE_NAME = "TOORCON19"
        val SHMOOCON_DATABASE_NAME = "SHMOOCON2018"
        val HACKWEST_DATABASE_NAME = "HACKWEST2018"
        val LAYERONE_DATABASE_NAME = "LAYERONE2018"
        val BSIDESORL_DATABASE_NAME = "BSIDESORL2018"

        val API_URL_BASE = "https://info.defcon.org/json/"
        val API_GITHUB_BASE = "https://raw.githubusercontent.com/BeezleLabs/conferences/master/"

        val TIME_SECONDS_IN_HOUR = 3600

        val DEBUG_FORCE_TIME_DATE = 1501355835000L
        val TIMER_INTERVAL_FIVE_MIN: Long = 300000
        val TIMER_INTERVAL_DEBUG: Long = 5000
        val DEBUG_PAUSE_TIME_SKIP = TIMER_INTERVAL_FIVE_MIN * 30

        val TIMER_INTERVAL = if (BuildConfig.DEBUG) TIMER_INTERVAL_DEBUG else TIMER_INTERVAL_FIVE_MIN

        val BOOKMARKED = 1
        val UNBOOKMARKED = 0

        val SYNC_MODE_AUTO = 0
        val SYNC_MDOE_MANUAL = 1

        val API_URL = "https://s3.amazonaws.com/defcon-api"
        val API_ERROR_MESSAGE = "\"errorMessage\":"
        val OFFICIAL_SCHEDULE = "/schedule-full.json"

        val FEEDBACK_EMAIL = "chrisporter0111@gmail.com"

    }
}
