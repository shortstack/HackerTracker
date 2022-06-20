package com.advice.schedule.database

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.advice.schedule.models.local.Event
import com.advice.schedule.network.task.ReminderWorker
import com.advice.schedule.utilities.MyClock
import com.advice.schedule.utilities.now
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

        val data = workDataOf(
            ReminderWorker.INPUT_ID to event.id,
            ReminderWorker.INPUT_CONFERENCE to event.conference
        )

        val notify = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(TAG + event.id)
            .build()

        workManager.enqueueUniqueWork(event.title, ExistingWorkPolicy.REPLACE, notify)
    }

    fun cancel(event: Event) {
        workManager.cancelAllWorkByTag(TAG + event.id)
    }
}