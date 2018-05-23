package com.shortstack.hackertracker.network.task

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.database.DEFCONDatabaseController
import com.shortstack.hackertracker.event.BusProvider
import com.shortstack.hackertracker.event.SyncResponseEvent
import com.shortstack.hackertracker.network.DatabaseService
import com.shortstack.hackertracker.network.SyncResponse
import com.shortstack.hackertracker.now
import com.shortstack.hackertracker.utils.NotificationHelper
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Inject

class SyncJob : JobService(), Callback<SyncResponse> {


    @Inject
    lateinit var notifications: NotificationHelper

    @Inject
    lateinit var storage: SharedPreferencesUtil

    @Inject
    lateinit var database: DEFCONDatabaseController

    var tag: String? = null

    init {
        App.application.myComponent.inject(this)
    }


    override fun onFailure(call: Call<SyncResponse>?, t: Throwable?) {
        Logger.e(t, "SyncJob failure.")
    }

    override fun onResponse(call: Call<SyncResponse>?, response: Response<SyncResponse>?) {
        if (response == null || !response.isSuccessful) {
            Logger.e("Syncing was not successful.")
            return
        }

        val body = response.body()
        if (body == null) {
            Logger.e("Syncing response body was null.")
            return
        }

        updateDatabase(body)
    }

    private fun updateDatabase(body: SyncResponse) {
        Logger.d("Tag: $tag")

        storage.lastRefresh = Date().now().time

        if (storage.lastUpdated != body.updatedDate) {
            storage.lastUpdated = body.updatedDate

            val rowsUpdated = database.updateSchedule(response = body)

            if (rowsUpdated > 0) {
                BusProvider.bus.post(SyncResponseEvent(rowsUpdated))
                notifications.scheduleUpdateNotification(rowsUpdated)
            }

        } else {
            Logger.d("Already up to date!")
        }
    }

    override fun onStartJob(job: JobParameters): Boolean {
        Logger.d("Start!")

        if (database.databaseName != Constants.DEFCON_DATABASE_NAME)
            return false

        val retrofit = Retrofit.Builder().baseUrl(Constants.API_URL_BASE).addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(DatabaseService::class.java)
        service.getScheduleJob.enqueue(this)

        tag = job.tag

        return false // Answers the question: "Is there still work going on?"
    }

    override fun onStopJob(job: JobParameters): Boolean {
        return false // Answers the question: "Should this job be retried?"
    }

    companion object {
        const val TAG = "sync_job"
    }


}
