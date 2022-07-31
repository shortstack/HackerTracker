package com.advice.schedule.network.task

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.utilities.NotificationHelper
import org.koin.core.KoinComponent
import org.koin.core.inject

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params),
    KoinComponent {

    private val database: DatabaseManager by inject()
    private val notifications: NotificationHelper by inject()

    override suspend fun doWork(): Result {
        val conference = inputData.getString(INPUT_CONFERENCE)
        if (conference == null) {
            // todo: log the error
            return Result.failure()
        }

        val id = inputData.getInt(INPUT_ID, -1)
        if (id == -1) {
            // todo: log the error
            return Result.failure()
        }

        val event = database.getEventById(conference, id)
        if (event == null) {
            // todo: log the error
            return Result.failure()
        }
        notifications.notifyStartingSoon(event)

        return Result.success()
    }

    companion object {
        const val INPUT_CONFERENCE = "INPUT_CONFERENCE"
        const val INPUT_ID = "INPUT_ID"
    }
}
