package com.shortstack.hackertracker.network.task

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.utils.NotificationHelper
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import javax.inject.Inject

class ReminderJob : JobService() {

    @Inject
    lateinit var notifications: NotificationHelper

    @Inject
    lateinit var storage: SharedPreferencesUtil


    override fun onStartJob(jobParameters: JobParameters): Boolean {
        App.application.myComponent.inject(this)

        if (!storage.allowPushNotification) return false

        val extras = jobParameters.extras
        val id = extras?.getInt(NOTIFICATION_ID)

        if (id != null)
            notifications.postNotification(id)

        return false
    }

    override fun onStopJob(jobParameters: JobParameters) = false

    companion object {
        private const val TAG = "reminder_job_"
        const val NOTIFICATION_ID = "notification-id"

        fun getTag(id: Int) = "$TAG$id"
    }
}
