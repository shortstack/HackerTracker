package com.shortstack.hackertracker.models


import com.shortstack.hackertracker.now
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class EventTest {

    private val current = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse("2019-01-01T12:00:00.000-0000")

    @Before
    fun before() {
        mockkStatic("com.shortstack.hackertracker.ExtensionsKt")

        every {
            Date(0).now()
        } returns SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse("2019-01-01T12:00:00.000-0000")

    }

    @After
    fun after() {

    }

    @Test
    fun hasNotStarted() {
        val event = FirebaseEvent(begin = "2019-01-01T14:00:00.000-0000", end = "2019-01-01T15:00:00.000-0000")

        assert(!event.hasStarted)
        assert(!event.hasFinished)
    }

    @Test
    fun hasFinished() {
        val event = FirebaseEvent(begin = "2019-01-01T08:00:00.000-0000", end = "2019-01-01T09:00:00.000-0000")

        assert(event.hasStarted)
        assert(event.hasFinished)
    }

    @Test
    fun inProgress() {
        val event = FirebaseEvent(title = "Test Event", begin = "2019-01-01T11:00:00.000-0000", end = "2019-01-01T13:00:00.000-0000")

        assert(event.hasStarted)
        assert(!event.hasFinished)
        assert(event.progress > 0.0)
        assert(event.progress < 1.0)
    }
}