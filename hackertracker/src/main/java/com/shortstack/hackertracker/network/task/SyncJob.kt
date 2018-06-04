package com.shortstack.hackertracker.network.task

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.network.DatabaseService
import com.shortstack.hackertracker.network.SyncResponse
import com.shortstack.hackertracker.utils.NotificationHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class SyncJob : JobService(), Callback<SyncResponse> {


    @Inject
    lateinit var notifications: NotificationHelper

    @Inject
    lateinit var database: DatabaseManager

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

//        if (storage.lastUpdated != body.updatedDate) {
//            storage.lastUpdated = body.updatedDate
//
//            database.updateConference(body = body)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({ rowsUpdated ->
//                        if (rowsUpdated > 0) {
//                            BusProvider.bus.post(SyncResponseEvent(rowsUpdated))
//                            notifications.scheduleUpdateNotification(rowsUpdated)
//                        }
//                    }, {
//                        Logger.e("Failed to update database,  ${it.message}")
//                    })
//
//        } else {
//            Logger.d("Already up to date!")
//        }
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

    companion object {
        const val TAG = "sync_job"
    }


}
