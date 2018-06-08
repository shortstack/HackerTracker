package com.shortstack.hackertracker.network.task

import androidx.work.Worker
import androidx.work.toWorkData
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.models.FAQs
import com.shortstack.hackertracker.models.response.Speakers
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors
import com.shortstack.hackertracker.network.DatabaseService
import com.shortstack.hackertracker.network.FullResponse
import com.shortstack.hackertracker.network.SyncResponse
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

    override fun doWork(): WorkerResult {
        val call = getService().getConferences()
        val conferences = call.execute().body()

        val list = database.getConferences()

        var rowsUpdated = 0

        conferences.conferences.forEach {

            val item = list.find { new -> new.conference.directory == it.directory }
            val isNewCon = item == null

            if (isNewCon || it.updated > item?.conference?.updated) {
                rowsUpdated += updateConference(it, isNewCon)
            }
        }

        outputData = mapOf(KEY_ROWS_UPDATED to rowsUpdated).toWorkData()

        return WorkerResult.SUCCESS
    }

    private fun updateConference(it: Conference, isNewCon: Boolean): Int {
        Logger.d("Fetching content for ${it.title}")

        try {
            val syncResponse = getSchedule(it.directory)
            val types = getTypes(it.directory)
            val vendors = getVendors(it.directory)
            val speakers = getSpeakers(it.directory)
            val faqs = getFAQs(it.directory)


            // Updating database
            val rowsUpdated = database.updateConference(it, FullResponse(syncResponse, types, speakers, vendors, faqs))

            notifications.notifyUpdates(it, isNewCon, rowsUpdated)
            Logger.e("Updated $rowsUpdated ows.")

            return rowsUpdated
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

//    fun getSchedule(databaseService: DatabaseService): Single<FullResponse> {
//        return zip(
//                databaseService.getSchedule,
//                databaseService.getTypes,
//
//                BiFunction<SyncResponse, Types, FullResponse> { schedule, types ->
//                    createModel(schedule, types)
//                })
//    }

    private fun getSchedule(directory: String): SyncResponse {
        val service = getService(directory)
        val call = service.getSchedule()
        return call.execute().body()
    }

    private fun getTypes(directory: String): Types {
        val service = getService(directory)
        val call = service.getTypes()
        return call.execute().body()
    }

    private fun getVendors(directory: String): Vendors {
        val service = getService(directory)
        val call = service.getVendors()
        return call.execute().body()
    }

    private fun getSpeakers(directory: String): Speakers {
        val service = getService(directory)
        val call = service.getSpeakers()
        return call.execute().body()
    }

    private fun getFAQs(directory: String): FAQs {
        val service = getService(directory)
        val call = service.getFAQs()
        return call.execute().body()
    }

    companion object {
        const val KEY_ROWS_UPDATED = "KEY_ROWS_UPDATED"

        const val TAG_SYNC = "TAG_SYNC"
    }
}