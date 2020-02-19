package com.shortstack.hackertracker.network.task

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.utilities.NotificationHelper
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params), KoinComponent {

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val TAG = "TAG_REMINDER_"
    }

    private val notifications: NotificationHelper by inject()
    private val database: DatabaseManager by inject()

    private var disposable: Disposable? = null

    override fun doWork(): Result {
        val id = inputData.getInt(NOTIFICATION_ID, -1)

        disposable = database.getEventById(id)
                .subscribe { event ->
                    notifications.notifyStartingSoon(event)
                }

        return Result.success()
    }

    override fun onStopped() {
        disposable?.dispose()
    }
}
