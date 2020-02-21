package com.shortstack.hackertracker.network.task

import androidx.work.Worker
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.utilities.NotificationHelper
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ReminderWorker : Worker(), KoinComponent {

    private val notifications: NotificationHelper by inject()

    private val database: DatabaseManager by inject()

    private var disposable: Disposable? = null

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
