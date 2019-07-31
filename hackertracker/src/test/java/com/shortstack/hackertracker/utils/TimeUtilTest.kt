package com.shortstack.hackertracker.utils

import android.content.Context
import android.text.format.DateFormat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shortstack.hackertracker.*
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.staticMockk
import io.mockk.use
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*


class TimeUtilTest {

    private val context: Context = mock()

    @Before
    fun before() {
        whenever(context.getString(R.string.today)).thenReturn("Today")
        whenever(context.getString(R.string.tomorrow)).thenReturn("Tomorrow")
        whenever(context.getString(R.string.tba)).thenReturn("TBA")
    }

    @Test
    fun isToday() {
        setCurrentClock("2019-01-01T12:00:00.000-0000")

        val date = parse("2019-01-01T12:00:00.000-0000")

        assertEquals(true, date.isToday())
        assertEquals(false, date.isTomorrow())
    }

    @Test
    fun isTomorrow() {
        setCurrentClock("2019-01-01T12:00:00.000-0000")

        val date = parse("2019-01-02T12:00:00.000-0000")

        assertEquals(false, date.isToday())
        assertEquals(true, date.isTomorrow())
    }

    @Test
    fun getRelativeToday() {
        setCurrentClock("2019-01-01T12:00:00.000-0000")

        val date = parse("2019-01-01T12:00:00.000-0000")

        val result = TimeUtil.getRelativeDateStamp(context, date)

        assertEquals("Today", result)
    }

    @Test
    fun getRelativeTomorrow() {
        setCurrentClock("2019-01-01T12:00:00.000-0000")
        val date = parse("2019-01-02T12:00:00.000-0000")

        val result = TimeUtil.getRelativeDateStamp(context, date)

        assertEquals("Tomorrow", result)
    }

    @Test
    fun getRelativeTimeStampNull() {
        val result = TimeUtil.getTimeStamp(context, null)

        assertEquals("TBA", result)
    }

    @Test
    fun getRelativeTimeStamp24() {
        val date = parse("2018-12-31T12:00:00.000-0000")
        mockkStatic(DateFormat::class)
        every { DateFormat.is24HourFormat(context) } returns true

        val result = TimeUtil.getTimeStamp(context, date)

        assertEquals("04:00", result)

    }

    @Test
    fun getRelativeTimeStampAM() {
        val date = parse("2018-12-31T12:00:00.000-0000")
        mockkStatic(DateFormat::class)
        every { DateFormat.is24HourFormat(context) } returns false

        val result = TimeUtil.getTimeStamp(context, date)

        assertEquals("4:00 AM", result)
    }
}