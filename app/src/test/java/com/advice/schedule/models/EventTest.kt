package com.advice.schedule.models

import com.advice.schedule.models.firebase.FirebaseEvent
import com.advice.schedule.models.firebase.FirebaseSpeaker
import com.advice.schedule.models.local.Event
import com.advice.schedule.parse
import com.advice.schedule.setCurrentClock
import com.advice.schedule.toEvent
import com.advice.schedule.utilities.getDateMidnight
import io.mockk.InternalPlatformDsl.toStr
import org.junit.Assert.assertEquals
import org.junit.Test

class EventTest {

    private val event: Event

    init {
        val firebase = FirebaseEvent(title = "Test Event", begin = "2019-01-01T12:00:00.000-0000", end = "2019-01-01T13:00:00.000-0000")
        firebase.speakers.add(FirebaseSpeaker(10, "John", "Tester"))
        event = firebase.toEvent()
    }

    @Test
    fun toEvent() {
        assertEquals("Test Event", event.title)
        assertEquals(1, event.speakers.size)
        assertEquals("John", event.speakers.first().name)
    }

    @Test
    fun hasNotStarted() {
        setCurrentClock("2019-01-01T11:00:00.000-0000")

        assertEquals(false, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(-1.0f, event.progress)
    }

    @Test
    fun hasJustStarted() {
        setCurrentClock("2019-01-01T12:00:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.0f, event.progress)
    }

    @Test
    fun is25PercentComplete() {
        setCurrentClock("2019-01-01T12:15:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.25f, event.progress)
    }

    @Test
    fun is50PercentComplete() {
        setCurrentClock("2019-01-01T12:30:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.50f, event.progress)
    }

    @Test
    fun is75PercentComplete() {
        setCurrentClock("2019-01-01T12:45:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.75f, event.progress)
    }

    @Test
    fun hasJustFinished() {
        setCurrentClock("2019-01-01T13:00:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(true, event.hasFinished)
        assertEquals(1.0f, event.progress)
    }

    @Test
    fun hasFinished() {
        setCurrentClock("2019-01-01T14:00:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(true, event.hasFinished)
        assertEquals(1.0f, event.progress)
    }

    @Test
    fun `converting date without timezone `() {
        val date = parse("2022-07-18T10:00:00.000-0700")

        val midnight = getDateMidnight(date)

        assertEquals("Mon Jul 18 00:00:00 PDT 2022", midnight.toString())
    }

    @Test
    fun `converting date with Chicago timezone`() {
        val date = parse("2022-07-18T10:00:00.000-0700")

        val midnight = getDateMidnight(date, "America/Chicago")

        assertEquals("Mon Jul 18 00:00:00 PDT 2022", midnight.toString())
    }
}