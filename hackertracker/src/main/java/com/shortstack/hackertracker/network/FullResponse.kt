package com.shortstack.hackertracker.network

import com.google.gson.Gson
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.fromFile
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.models.Events
import com.shortstack.hackertracker.models.FAQs
import com.shortstack.hackertracker.models.Locations
import com.shortstack.hackertracker.models.response.Speakers
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors

class FullResponse(val types: Types?, val locations: Locations?, val speakers: Speakers?, val events: Events?, val vendors: Vendors?, val faqs: FAQs?) {

    companion object {
        fun getLocalFullResponse(conference: Conference): FullResponse {
            val gson = Gson()
            val database = conference.code

            return FullResponse(gson.fromFile<Types>(Constants.TYPES_FILE, root = database),
                    gson.fromFile<Locations>(Constants.LOCATIONS_FILE, root = database),
                    gson.fromFile<Speakers>(Constants.SPEAKERS_FILE, root = database),
                    gson.fromFile<Events>(Constants.SCHEDULE_FILE, root = database),
                    gson.fromFile<Vendors>(Constants.VENDORS_FILE, root = database),
                    gson.fromFile<FAQs>(Constants.FAQ_FILE, root = database))
        }
    }
}