package com.shortstack.hackertracker.network.task

import androidx.work.Worker
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.utilities.NotificationHelper
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.runBlocking
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ReminderWorker : Worker(), KoinComponent {

    private val database: DatabaseManager by inject()
    private val notifications: NotificationHelper by inject()

    override fun doWork(): Result {
        val conference = inputData.getString(INPUT_CONFERENCE, null)
        val id = inputData.getInt(INPUT_ID, -1)

        if (conference == null || id == -1) {
            return Result.FAILURE
        }

        runBlocking {
            val event = database.getEventById(conference, id)
            if (event != null) {
                notifications.notifyStartingSoon(event)
            }
        }

        return Result.SUCCESS
    }

    companion object {
        const val INPUT_CONFERENCE = "INPUT_CONFERENCE"
        const val INPUT_ID = "INPUT_ID"
    }
}
