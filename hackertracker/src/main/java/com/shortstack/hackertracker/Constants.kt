package com.shortstack.hackertracker

object Constants {

    const val API_GITHUB_BASE = "https://rawgit.com/BeezleLabs/conferences/master/"

    private const val AUG_09_2018 = 1533801600000L
    const val DEBUG_FORCE_TIME_DATE = AUG_09_2018

    // TODO: Replace this with a feedback email.
    const val FEEDBACK_EMAIL = "chrisporter0111@gmail.com"


    // File names
    const val CONFERENCES_FILE = "conferences.json"

    const val SCHEDULE_FILE = "events.json"
    const val TYPES_FILE = "event_types.json"
    const val SPEAKERS_FILE = "speakers.json"
    const val VENDORS_FILE = "vendors.json"
    const val FAQ_FILE = "faq.json"
}
