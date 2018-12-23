package com.shortstack.hackertracker.network.task

import androidx.work.Worker
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.utils.NotificationHelper
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
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

        // TODO: Handle the LiveData Observer.
//        database.getEventById(id = id)?.observe(this, Observer {
//
//            notifications.notifyStartingSoon(event)
//
//        })


        return Result.SUCCESS
    }

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val TAG = "TAG_REMINDER_"
    }
}
