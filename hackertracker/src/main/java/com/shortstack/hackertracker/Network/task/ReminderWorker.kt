package com.shortstack.hackertracker.network.task

import androidx.work.Worker
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.utils.NotificationHelper
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ReminderWorker : Worker() {

    @Inject
    lateinit var notifications: NotificationHelper

    @Inject
    lateinit var storage: SharedPreferencesUtil

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
    }

    override fun doWork(): Result {
        if (!storage.allowPushNotification)
            return Result.SUCCESS

        val id = inputData.getInt(NOTIFICATION_ID, -1)
        val event = database.findItem(id = id)

        if (event != null)
            notifications.notifyStartingSoon(event)

        return Result.SUCCESS
    }

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val TAG = "TAG_REMINDER_"
    }
}
