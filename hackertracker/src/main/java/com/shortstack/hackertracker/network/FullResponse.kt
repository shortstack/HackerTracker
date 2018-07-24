package com.shortstack.hackertracker.network

import com.google.gson.Gson
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.database.ConferenceFile
import com.shortstack.hackertracker.fromFile
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.models.response.Speakers
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors

class FullResponse(val types: Types?, val locations: Locations?, val speakers: Speakers?, val events: Events?, val vendors: Vendors?, val faqs: FAQs?) {

    fun isEmpty() = types == null && locations == null && speakers == null && events == null && vendors == null && faqs == null

    fun isNotEmpty() = !isEmpty()

    companion object {
        fun getLocalFullResponse(conference: Conference, localConference: Conference?): FullResponse {
            val gson = Gson()
            val root = conference.code

            return FullResponse(
                    getUpdate<Types>(gson, root, Constants.TYPES_FILE, conference.types, localConference?.types),
                    getUpdate<Locations>(gson, root, Constants.LOCATIONS_FILE, conference.locations, localConference?.locations),
                    getUpdate<Speakers>(gson, root, Constants.SPEAKERS_FILE, conference.speakers, localConference?.speakers),
                    getUpdate<Events>(gson, root, Constants.EVENTS_FILE, conference.events, localConference?.events),
                    getUpdate<Vendors>(gson, root, Constants.VENDORS_FILE, conference.vendors, localConference?.vendors),
                    getUpdate<FAQs>(gson, root, Constants.FAQ_FILE, conference.faqs, localConference?.faqs))
        }

        private inline fun <reified T> getUpdate(gson: Gson, root: String, file: String, conference: ConferenceFile?, localConference: ConferenceFile?): T? {
            if (isStale(conference, localConference))
                return gson.fromFile<T>(file, root = root)
            return null
        }

        private fun isStale(conference: ConferenceFile?, local: ConferenceFile?): Boolean {
            return conference == null || local == null || conference.updatedAt > local.updatedAt
        }

    }
}