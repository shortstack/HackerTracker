package com.shortstack.hackertracker.network.task

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.utilities.NotificationHelper
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params),
    KoinComponent {

    private val database: DatabaseManager by inject()
    private val notifications: NotificationHelper by inject()

    override fun doWork(): Result {
        val conference = inputData.getString(INPUT_CONFERENCE)
        val id = inputData.getInt(INPUT_ID, -1)

        if (conference == null || id == -1) {
            return Result.failure()
        }

        runBlocking {
            val event = database.getEventById(conference, id)
            if (event != null) {
                notifications.notifyStartingSoon(event)
            }
        }

        return Result.success()
    }

    companion object {
        const val INPUT_CONFERENCE = "INPUT_CONFERENCE"
        const val INPUT_ID = "INPUT_ID"
    }
}
