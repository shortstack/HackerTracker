package com.shortstack.hackertracker.Common

import com.shortstack.hackertracker.BuildConfig

interface Constants {
    companion object {

        val DEFCON_DATABASE_NAME = "DEFCON25"
        val TOORCON_DATABASE_NAME = "TOORCON19"

        val API_URL_BASE = "https://info.defcon.org/json/"
        val API_GITHUB_BASE = "https://raw.githubusercontent.com/BeezleLabs/conferences/master/"


        val TIME_SECONDS_IN_HOUR = 3600

        val DEBUG_FORCE_TIME_DATE = 1501355835000L
        val TIMER_INTERVAL_FIVE_MIN : Long = 300000
        val TIMER_INTERVAL_DEBUG : Long = 5000
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

        val UBER_CLIENT_ID = "q1eUCeC1ZenbGmQD2vb0MytALvboEHhD"

        val UBER_PARIS = "Paris"
        val UBER_BALLYS = "Bally's"
        val UBER_TUSCANY = "Tuscany Suites & Casino"
        val UBER_CAESARS = "Caesar's Palace"
        val UBER_MANDALAY = "Mandalay Bay"
        val UBER_BELLAGIO = "Bellagio"
        val UBER_PLANET_HOLLYWOOD = "Planet Hollywood"

        val UBER_DEFAULT = "Choose your destination"

        val UBER_ADDRESS_PARIS = "3655 S Las Vegas Blvd, Las Vegas, NV 89109"
        val UBER_ADDRESS_BALLYS = "3645 S Las Vegas Blvd, Las Vegas, NV 89109"
        val UBER_ADDRESS_TUSCANY = "255 E Flamingo Rd, Las Vegas, NV 89169"
        val UBER_ADDRESS_CAESARS = "3570 S Las Vegas Blvd, Las Vegas, NV 89109"
        val UBER_ADDRESS_MANDALAY = "3950 S Las Vegas Blvd, Las Vegas, NV 89119"
        val UBER_ADDRESS_BELLAGIO = "3600 S Las Vegas Blvd, Las Vegas, NV 89109"
        val UBER_ADDRESS_PLANET_HOLLYWOOD = "3667 S Las Vegas Blvd, Las Vegas, NV 89109"

        val UBER_LOCATIONS = arrayOf(UBER_PARIS, UBER_BALLYS, UBER_TUSCANY, UBER_PLANET_HOLLYWOOD, UBER_CAESARS, UBER_MANDALAY, UBER_BELLAGIO, UBER_DEFAULT)
        val UBER_ADDRESSES = arrayOf(UBER_ADDRESS_PARIS, UBER_ADDRESS_BALLYS, UBER_ADDRESS_TUSCANY, UBER_ADDRESS_PLANET_HOLLYWOOD, UBER_ADDRESS_CAESARS, UBER_ADDRESS_MANDALAY, UBER_ADDRESS_BELLAGIO, UBER_ADDRESS_PARIS)
    }
}