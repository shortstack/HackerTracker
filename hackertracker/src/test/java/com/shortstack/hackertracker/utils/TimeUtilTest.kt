package com.shortstack.hackertracker.utils

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.isSoonish
import com.shortstack.hackertracker.isToday
import com.shortstack.hackertracker.isTomorrow
import io.mockk.every
import io.mockk.mockkStatic
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
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:00:00.000-0000")

        val date = parse("2019-01-01T12:00:00.000-0000")

        assertEquals(true, date.isToday())
        assertEquals(false, date.isTomorrow())
        assertEquals(true, date.isSoonish(5))
    }

    @Test
    fun isTomorrow() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:00:00.000-0000")

        val date = parse("2019-01-02T12:00:00.000-0000")

        assertEquals(false, date.isToday())
        assertEquals(true, date.isTomorrow())
        assertEquals(true, date.isSoonish(5))
    }

    @Test
    fun isSoonish() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:00:00.000-0000")

        val date = parse("2019-01-04T12:00:00.000-0000")

        assertEquals(false, date.isToday())
        assertEquals(false, date.isTomorrow())
        assertEquals(true, date.isSoonish(5))
    }

    @Test
    fun getRelativeToday() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:00:00.000-0000")

        val date = parse("2019-01-01T12:00:00.000-0000")

        val result = TimeUtil.getRelativeDateStamp(context, date)

        assertEquals("Today", result)
    }

    @Test
    fun getRelativeTomorrow() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:00:00.000-0000")
        val date = parse("2019-01-02T12:00:00.000-0000")

        val result = TimeUtil.getRelativeDateStamp(context, date)

        assertEquals("Tomorrow", result)
    }

    @Test
    fun getRelativeSoonish() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:00:00.000-0000")
        val date = parse("2019-01-04T12:00:00.000-0000")

        val result = TimeUtil.getRelativeDateStamp(context, date)

        assertEquals("Friday", result)
    }

    @Test
    fun getRelativeNotSoonish() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:00:00.000-0000")
        val date = parse("2019-01-10T12:00:00.000-0000")

        val result = TimeUtil.getRelativeDateStamp(context, date)

        assertEquals("January 10", result)
    }

    @Test
    fun getRelativeYesterday() {
        mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
        every {
            MyClock().now()
        } returns parse("2019-01-01T12:00:00.000-0000")
        val date = parse("2018-12-31T12:00:00.000-0000")

        val result = TimeUtil.getRelativeDateStamp(context, date)

        assertEquals("December 31", result)
    }

    private fun parse(date: String): Date {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date)
    }
}