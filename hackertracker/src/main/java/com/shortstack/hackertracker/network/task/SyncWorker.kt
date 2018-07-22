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
import com.shortstack.hackertracker.database.ConferenceFile
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

            val item = list.find { new -> new.conference.code == it.code }
            val isNewCon = item == null

            it.isSelected = item?.conference?.isSelected ?: false

            rowsUpdated += updateConference(it, item, isNewCon)
        }

        outputData = mapOf(KEY_ROWS_UPDATED to rowsUpdated).toWorkData()

        return Result.SUCCESS
    }

    private fun updateConference(stale: Conference, item: DatabaseConference?, isNewCon: Boolean): Int {
        Logger.d("Fetching content for ${stale.name}")

        try {
            val updatedAt = stale.updated

            val conference = item?.conference

            val response = FullResponse(
                    getUpdate<Types>(stale.code, stale.types, conference?.types, TYPES_FILE),
                    getUpdate<Locations>(stale.code, stale.locations, conference?.locations, LOCATIONS_FILE),
                    getUpdate<Speakers>(stale.code, stale.speakers, conference?.speakers, SPEAKERS_FILE),
                    getUpdate<Events>(stale.code, stale.events, conference?.events, EVENTS_FILE),
                    getUpdate<Vendors>(stale.code, stale.vendors, conference?.vendors, VENDORS_FILE),
                    getUpdate<FAQs>(stale.code, stale.faqs, conference?.faqs, FAQ_FILE))

            // Updating database
            database.updateConference(stale, response)

            val count = database.getUpdatedEventsCount(updatedAt)
            val updatedBookmarks = database.getUpdatedBookmarks(stale, updatedAt)

            notifications.notifyUpdates(stale, isNewCon, count)
            notifications.updatedBookmarks(updatedBookmarks)

            Logger.e("Updated $count rows.")

            return count
        } catch (ex: Exception) {
            Logger.e("Exception! ${ex.message}")
        }
        return -1
    }


    private inline fun <reified T> getUpdate(directory: String, file: ConferenceFile?, other: ConferenceFile?, url: String): T? {
        if (isStale(file, other))
            return getSource<T>(directory, url)
        return null
    }

    private fun isStale(file: ConferenceFile?, other: ConferenceFile?): Boolean {
        return file == null || other == null || file.updatedAt < other.updatedAt
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
                .baseUrl(Constants.API_GITHUB_BASE + directory)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(DatabaseService::class.java)
    }

    companion object {
        const val KEY_ROWS_UPDATED = "KEY_ROWS_UPDATED"

        const val TAG_SYNC = "TAG_SYNC"
    }
}