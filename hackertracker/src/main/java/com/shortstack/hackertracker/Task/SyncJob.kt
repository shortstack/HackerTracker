package com.shortstack.hackertracker.Task

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.Event.SyncResponseEvent
import com.shortstack.hackertracker.Network.DatabaseService
import com.shortstack.hackertracker.Network.SyncResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SyncJob : JobService(), Callback<SyncResponse> {

    companion object {
        val TAG = "sync_job"
    }

    var tag : String? = null


    override fun onFailure(call: Call<SyncResponse>?, t: Throwable?) {
        Logger.e(t, "SyncJob failure.")
    }

    override fun onResponse(call: Call<SyncResponse>?, response: Response<SyncResponse>?) {
        if (response == null || !response.isSuccessful) {
            Logger.d("Syncing was not successful.")
            return
        }

        updateDatabase(response.body()!!)
    }

    fun updateDatabase(body: SyncResponse) {
        val storage = App.application.storage

        Logger.d("Tag: " + tag)

        storage.lastRefresh = App.getCurrentDate().time

        if (storage.lastUpdated != body.updatedDate) {
            storage.lastUpdated = body.updatedDate

            val database = App.application.databaseController

            val rowsUpdated = database.updateSchedule(response = body)

            if (rowsUpdated > 0) {
                App.application.postBusEvent(SyncResponseEvent(rowsUpdated))
                App.application.notificationHelper.scheduleUpdateNotification(rowsUpdated)
            }

        } else {
            Logger.d("Already up to date!")
        }
    }

    override fun onStartJob(job: JobParameters): Boolean {
        Logger.d("Start!")

        val retrofit = Retrofit.Builder().baseUrl(Constants.API_URL_BASE).addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(DatabaseService::class.java)
        service.getScheduleJob.enqueue(this)

        tag = job.tag

        return false // Answers the question: "Is there still work going on?"
    }

    override fun onStopJob(job: JobParameters): Boolean {
        return false // Answers the question: "Should this job be retried?"
    }
}
