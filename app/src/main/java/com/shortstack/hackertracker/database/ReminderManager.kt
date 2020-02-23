package com.shortstack.hackertracker.database

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.toWorkData
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.network.task.ReminderWorker
import com.shortstack.hackertracker.utilities.MyClock
import com.shortstack.hackertracker.utilities.now
import java.util.concurrent.TimeUnit

class ReminderManager(
    private val databaseManager: DatabaseManager,
    private val workManager: WorkManager
) {

    companion object {
        private const val TWENTY_MINUTES_BEFORE = 1000 * 20 * 60
        private const val TAG = "reminder_"
    }

    suspend fun getEvent(conference: String, id: Int): Event? {
        return databaseManager.getEventById(conference, id)
    }

    fun setReminder(event: Event) {
        val start = event.start
        val now = MyClock().now()

        val delay = start.time - now.time - TWENTY_MINUTES_BEFORE

        if (delay < 0) {
            return
        }

        val data = mapOf(
            ReminderWorker.INPUT_ID to event.id,
            ReminderWorker.INPUT_CONFERENCE to event.conference
        ).toWorkData()

        val notify = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(TAG + event.id)
            .build()

        workManager.enqueue(notify)
    }

    fun cancel(event: Event) {
        workManager.cancelAllWorkByTag(TAG + event.id)
    }
}