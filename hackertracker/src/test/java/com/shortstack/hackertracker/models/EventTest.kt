package com.shortstack.hackertracker.models

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class EventTest {

    private val event = FirebaseEvent(title = "Test Event", begin = "2019-01-01T12:00:00.000-0000", end = "2019-01-01T13:00:00.000-0000")

    @Before
    fun before() {

    }

    // TODO: Inject current Date with Koin into the Event.
    @Test
    fun hasNotStarted() {
        val current = parse("2019-01-01T11:00:00.000-0000")

        assertEquals(false, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(-1.0, event.progress)
    }

    @Test
    fun hasJustStarted() {
        val current = parse("2019-01-01T12:00:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.0, event.progress)
    }

    @Test
    fun is25PercentComplete() {
        val current = parse("2019-01-01T12:15:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.25, event.progress)
    }

    @Test
    fun is50PercentComplete() {
        val current = parse("2019-01-01T12:30:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.50, event.progress)
    }

    @Test
    fun is75PercentComplete() {
        val current = parse("2019-01-01T12:45:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.75, event.progress)
    }

    @Test
    fun hasJustFinished() {
        val current = parse("2019-01-01T13:00:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(true, event.hasFinished)
        assertEquals(1.0, event.progress)
    }

    @Test
    fun hasFinished() {
        val current = parse("2019-01-01T14:00:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(true, event.hasFinished)
        assertEquals(1.0, event.progress)
    }


    private fun parse(date: String): Date {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date)
    }

}