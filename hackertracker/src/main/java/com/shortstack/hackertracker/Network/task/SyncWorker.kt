package com.shortstack.hackertracker.network.task

import androidx.work.Data
import androidx.work.Worker
import androidx.work.toWorkData
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.network.DatabaseService
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

            val item = list.find { new -> new.directory == it.directory }
            val isNewCon = item == null

            if (isNewCon || it.updated > item?.updated) {
                rowsUpdated += updateConference(it, isNewCon)
            }
        }

        outputData = mapOf(KEY_ROWS_UPDATED to rowsUpdated).toWorkData()

        return WorkerResult.SUCCESS
    }

    private fun updateConference(it: Conference, isNewCon: Boolean): Int {
        Logger.d("Fetching content for ${it.title}")

        val call = getService(it.directory).getSchedule()
        val syncResponse = call.execute().body()

        // Updating database
        val rowsUpdated = database.updateConference(it, syncResponse)

        notifications.notifyUpdates(it, isNewCon, rowsUpdated)
        Logger.e("Updated $rowsUpdated ows.")

        return rowsUpdated
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