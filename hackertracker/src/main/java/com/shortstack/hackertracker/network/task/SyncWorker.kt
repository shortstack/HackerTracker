package com.shortstack.hackertracker.network.task

import androidx.work.Worker
import androidx.work.toWorkData
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
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

    private fun updateConference(it: Conference, item: DatabaseConference?, isNewCon: Boolean): Int {
        Logger.d("Fetching content for ${it.name}")

        try {
            val updatedAt = it.updated

            val events = getEvents(it.code)
            val types = if (it.types == null || item?.conference?.types == null || it.types.updatedAt < item.conference.types.updatedAt) getTypes(it.code) else null
            val locations = getLocations(it.code)
            val vendors = getVendors(it.code)
            val speakers = getSpeakers(it.code)
            val faqs = if (it.faqs == null || item?.conference?.faqs == null || it.faqs.updatedAt < item.conference.faqs.updatedAt) getFAQs(it.code) else null


            // Updating database
            database.updateConference(it, FullResponse(types, locations, speakers, events, vendors, faqs))

            val count = database.getUpdatedEventsCount(updatedAt)
            val updatedBookmarks = database.getUpdatedBookmarks(it, updatedAt)


            notifications.notifyUpdates(it, isNewCon, count)
            notifications.updatedBookmarks(updatedBookmarks)


            Logger.e("Updated $count rows.")

            return count
        } catch (ex: Exception) {
            Logger.e("Exception! ${ex.message}")
        }
        return -1
    }

    private fun getService(directory: String? = null): DatabaseService {
        val directory = if (directory != null) "$directory/" else ""
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.API_GITHUB_BASE + directory)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(DatabaseService::class.java)
    }

//    fun getEvents(databaseService: DatabaseService): Single<FullResponse> {
//        return zip(
//                databaseService.getEvents,
//                databaseService.getTypes,
//
//                BiFunction<SyncResponse, Types, FullResponse> { schedule, types ->
//                    createModel(schedule, types)
//                })
//    }


    private fun getTypes(directory: String): Types? {
        val service = getService(directory)
        val call = service.getTypes()
        return call.execute().body()
    }

    private fun getLocations(directory: String): Locations? {
        val service = getService(directory)
        val call = service.getLocations()
        return call.execute().body()
    }


    private fun getSpeakers(directory: String): Speakers? {
        val service = getService(directory)
        val call = service.getSpeakers()
        return call.execute().body()
    }

    private fun getEvents(directory: String): Events? {
        val service = getService(directory)
        val call = service.getSchedule()
        return call.execute().body()
    }

    private fun getVendors(directory: String): Vendors? {
        val service = getService(directory)
        val call = service.getVendors()
        return call.execute().body()
    }

    private fun getFAQs(directory: String): FAQs? {
        val service = getService(directory)
        val call = service.getFAQs()
        return call.execute().body()
    }

    companion object {
        const val KEY_ROWS_UPDATED = "KEY_ROWS_UPDATED"

        const val TAG_SYNC = "TAG_SYNC"
    }
}