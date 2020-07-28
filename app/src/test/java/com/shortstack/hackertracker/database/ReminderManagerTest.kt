package com.shortstack.hackertracker.database

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.nhaarman.mockitokotlin2.*
import com.shortstack.hackertracker.models.firebase.FirebaseEvent
import com.shortstack.hackertracker.setCurrentClock
import com.shortstack.hackertracker.toEvent
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ReminderManagerTest {

    private val database = mock<DatabaseManager>()
    private val work = mock<WorkManager>()

    private val manager = ReminderManager(database, work)

    @Before
    fun setUp() {
        setCurrentClock("2020-01-01T00:00:00.000-0000")
    }

    @Test
    fun `get event for notification`() = runBlocking {
        val event = FirebaseEvent(12, "con", "event", begin = "2020-01-01T00:20:00.000-0000", end =  "2020-01-01T00:20:00.000-0000").toEvent()

        whenever(database.getEventById("con", 12)).thenReturn(event)

        val result = manager.getEvent("con", 12)

        assertEquals(event.id, result?.id)
    }

    @Test
    fun `set reminder when in 20 minutes or more`() {
        val event = FirebaseEvent(12, "con", "event", begin = "2020-01-01T00:20:00.000-0000", end =  "2020-01-01T00:20:00.000-0000").toEvent()

        manager.setReminder(event)

        verify(work).enqueue(any<OneTimeWorkRequest>())
    }

    @Test
    fun `don't set reminder if less than 20 minutes`() {
        val event = FirebaseEvent(12, "con", "event", begin = "2020-01-01T00:05:00.000-0000", end =  "2020-01-01T00:20:00.000-0000").toEvent()

        manager.setReminder(event)

        verify(work, never()).enqueue(any<OneTimeWorkRequest>())
    }

    @Test
    fun `update reminder when event is updated`() {
        val event = FirebaseEvent(12, "con", "event", begin = "2020-01-01T00:20:00.000-0000", end =  "2020-01-01T00:20:00.000-0000").toEvent()
        val updated = FirebaseEvent(12, "con", "event", begin = "2020-01-01T00:25:00.000-0000", end =  "2020-01-01T00:20:00.000-0000").toEvent()

        manager.setReminder(event)
        manager.setReminder(updated)

        verify(work, times(2)).enqueue(any<OneTimeWorkRequest>())
    }

    @Test
    fun `clear reminder when unbookmarked`() {
        val event = FirebaseEvent(12, "con", "event", begin = "2020-01-01T00:20:00.000-0000", end =  "2020-01-01T00:20:00.000-0000").toEvent()

        manager.cancel(event)

        verify(work, never()).enqueue(any<OneTimeWorkRequest>())
        verify(work).cancelAllWorkByTag("reminder_12")
    }
}