package com.shortstack.hackertracker.network.task

import androidx.work.Worker
import androidx.work.toWorkData
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.Constants.EVENTS_FILE
import com.shortstack.hackertracker.Constants.FAQ_FILE
import com.shortstack.hackertracker.Constants.LOCATIONS_FILE
import com.shortstack.hackertracker.Constants.SPEAKERS_FILE
import com.shortstack.hackertracker.Constants.TYPES_FILE
import com.shortstack.hackertracker.Constants.VENDORS_FILE
import com.shortstack.hackertracker.models.ConferenceFile
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.models.response.Speakers
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors
import com.shortstack.hackertracker.network.DatabaseService
import com.shortstack.hackertracker.network.FullResponse
import com.shortstack.hackertracker.utils.NotificationHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * Created by Chris on 6/6/2018.
 */
class SyncWorker : Worker() {

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var notifications: NotificationHelper

    init {
        App.application.component.inject(this)
    }

    override fun doWork(): Result {
        val call = getService().getConferences()
        val conferences = call.execute().body()

        val list = database.getConferences()

        var rowsUpdated = 0

        conferences?.conferences?.forEach {
            val localConference = list.find { new -> new.conference.code == it.code }
            rowsUpdated += updateConference(it, localConference)
        }

        outputData = mapOf(KEY_ROWS_UPDATED to rowsUpdated).toWorkData()

        return Result.SUCCESS
    }

    private fun updateConference(conference: Conference, localConference: DatabaseConference?): Int {
        Logger.d("Fetching content for ${conference.name}")

        val local = localConference?.conference

        val response = FullResponse(
                getUpdate<Types>(conference.code, conference.types, local?.types, TYPES_FILE),
                getUpdate<Locations>(conference.code, conference.locations, local?.locations, LOCATIONS_FILE),
                getUpdate<Speakers>(conference.code, conference.speakers, local?.speakers, SPEAKERS_FILE),
                getUpdate<Events>(conference.code, conference.events, local?.events, EVENTS_FILE),
                getUpdate<Vendors>(conference.code, conference.vendors, local?.vendors, VENDORS_FILE),
                getUpdate<FAQs>(conference.code, conference.faqs, local?.faqs, FAQ_FILE))

        if (response.isNotEmpty()) {
            // Updating database
            conference.isSelected = local?.isSelected ?: false
            database.updateConference(conference, response)

            val updatedAt = local?.events?.updatedAt

            val count = database.getUpdatedEventsCount(updatedAt)
//            val updatedBookmarks = database.getUpdatedBookmarks(conference, updatedAt)
//
//            notifications.notifyUpdates(conference, localConference == null, count)
//            notifications.updatedBookmarks(updatedBookmarks)

            Logger.d("Updated $count rows.")
            return count
        }

        return 0
    }


    private inline fun <reified T> getUpdate(directory: String, file: ConferenceFile?, other: ConferenceFile?, url: String): T? {
        if (isStale(file, other))
            return getSource<T>(directory, url)
        return null
    }

    private fun isStale(network: ConferenceFile?, local: ConferenceFile?): Boolean {
        return network == null || local == null || network.updatedAt > local.updatedAt
    }

    private inline fun <reified T> getSource(directory: String, url: String): T? {
        val service = getService(directory)
        val call = service.getSource(url)
        val body = call.execute().body()
        return Gson().fromJson(body, T::class.java)
    }

    private fun getService(directory: String? = null): DatabaseService {
        val directory = if (directory != null) "$directory/" else ""
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.API_BASE + directory)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(DatabaseService::class.java)
    }

    companion object {
        const val KEY_ROWS_UPDATED = "KEY_ROWS_UPDATED"

        const val TAG_SYNC = "TAG_SYNC"
    }
}