package com.shortstack.hackertracker.network.task

import android.annotation.SuppressLint
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.network.DatabaseService
import com.shortstack.hackertracker.utils.NotificationHelper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class SyncJob : JobService() {

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var notificationHelper: NotificationHelper

    init {
        App.application.component.inject(this)
    }

    @SuppressLint("CheckResult")
    override fun onStartJob(job: JobParameters): Boolean {
        Logger.d("Start!")

        Single.fromCallable {
            val response = getService().getSyncConferencesJob.execute()
            val body = response.body()

            Logger.d("Got conferences!")

            val list = database.getConsBackground()

            // Updating each conference
            body.conferences.forEach {

                Logger.d("Looking at ${it.title}.")

                val item = list.find { new -> new.directory == it.directory }
                if (item == null || it.updated > item.updated) {

                    Logger.d("Fetching content for ${it.title}")
                    val response = getService(it.directory).getScheduleBackground.execute()
                    val body = response.body()

                    Logger.d("Now updating conferences.")

                    database.updateConference(it, body)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({

                                rowsUpdated ->

                                notificationHelper.notifyUpdates(it, item == null, rowsUpdated)
                            }, {
                                Logger.e("Error ${it.message}")
                            })
                }
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Logger.e("Complete syncing.")
                }, {
                    Logger.e("Error ${it.message}")
                })



        return false
    }

    private fun getService(directory: String? = null): DatabaseService {
        val directory = if (directory != null) "$directory/" else ""
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.API_GITHUB_BASE + directory)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(DatabaseService::class.java)
    }

    override fun onStopJob(job: JobParameters) = false

    companion object {
        const val TAG = "sync_job"
    }
}
