package com.shortstack.hackertracker.models


import com.shortstack.hackertracker.now
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*

class EventTest {

    @Before
    fun before() {
        mockkStatic("com.shortstack.hackertracker.ExtensionsKt")

        every {
            Date().now()
        } returns Date(1556998767513) // Sat May 04 12:41:07 PDT 2019
    }

    @After
    fun after() {

    }

    @Test
    fun hasNotStarted() {
        val event = FirebaseEvent(begin = "2019-05-04T21:00:00.000-0000", end = "2019-05-04T22:00:00.000-0000")

        assert(!event.hasStarted)
        assert(!event.hasFinished)
    }

    @Test
    fun hasFinished() {
        val event = FirebaseEvent(begin = "2019-05-03T16:00:00.000-0000", end = "2019-05-03T16:30:00.000-0000")

        assert(event.hasStarted)
        assert(event.hasFinished)
    }

    @Test
    fun getProgress() {
        val event = FirebaseEvent(title = "Test Event", begin = "2019-05-03T14:30:00.000-0000", end = "2019-05-04T22:00:00.000-0000")

        assert(event.hasStarted)
        assert(!event.hasFinished)
        assert(event.progress > 0.0)
        assert(event.progress < 1.0)
    }
}