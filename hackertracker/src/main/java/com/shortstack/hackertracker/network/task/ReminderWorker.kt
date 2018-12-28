package com.shortstack.hackertracker.network.task

import androidx.work.Worker
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.utils.NotificationHelper
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ReminderWorker : Worker() {

    @Inject
    lateinit var notifications: NotificationHelper

    @Inject
    lateinit var database: DatabaseManager

    private var disposable: Disposable? = null

    init {
        App.application.component.inject(this)
    }

    override fun doWork(): Result {
        val id = inputData.getInt(NOTIFICATION_ID, -1)

        disposable = database.getEventById(id)
                .subscribe { event ->
                    notifications.notifyStartingSoon(event)
                }

        return Result.SUCCESS
    }

    override fun onStopped(cancelled: Boolean) {
        disposable?.dispose()
    }

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val TAG = "TAG_REMINDER_"
    }
}
