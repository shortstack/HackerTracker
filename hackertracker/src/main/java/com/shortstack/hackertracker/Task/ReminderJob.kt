package com.shortstack.hackertracker.Task

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.shortstack.hackertracker.Application.App

class ReminderJob : JobService() {

    companion object {
        val TAG = "reminder_job_"
        val NOTIFICATION_ID = "notification-id"

        fun getTag(id: Int): String {
            return "$TAG$id"
        }
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        if (App.Companion.application.storage.allowPushNotifications()) {

            val extras = jobParameters.extras
            val id = extras?.getInt(NOTIFICATION_ID)

            if (id != null)
                App.application.notificationHelper.postNotification(id)
        }
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }
}
