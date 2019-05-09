package com.shortstack.hackertracker.models

import com.shortstack.hackertracker.models.firebase.FirebaseEvent
import com.shortstack.hackertracker.toEvent
import com.shortstack.hackertracker.utils.MyClock
import com.shortstack.hackertracker.utils.now
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class EventTest {

    private val event = FirebaseEvent(title = "Test Event", begin = "2019-01-01T12:00:00.000-0000", end = "2019-01-01T13:00:00.000-0000").toEvent()

    @Before
    fun before() {

    }

    @Test
    fun hasNotStarted() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T11:00:00.000-0000")

        assertEquals(false, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(-1.0f, event.progress)
    }

    @Test
    fun hasJustStarted() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:00:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.0f, event.progress)
    }

    @Test
    fun is25PercentComplete() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:15:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.25f, event.progress)
    }

    @Test
    fun is50PercentComplete() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:30:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.50f, event.progress)
    }

    @Test
    fun is75PercentComplete() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:45:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(false, event.hasFinished)
        assertEquals(0.75f, event.progress)
    }

    @Test
    fun hasJustFinished() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T13:00:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(true, event.hasFinished)
        assertEquals(1.0f, event.progress)
    }

    @Test
    fun hasFinished() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T14:00:00.000-0000")

        assertEquals(true, event.hasStarted)
        assertEquals(true, event.hasFinished)
        assertEquals(1.0f, event.progress)
    }


    private fun parse(date: String): Date {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date)
    }

}