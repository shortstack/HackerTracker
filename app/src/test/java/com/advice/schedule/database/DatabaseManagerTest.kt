package com.advice.schedule.database

import com.advice.schedule.fromString
import com.advice.schedule.models.firebase.FirebaseConference
import com.advice.schedule.models.local.Conference
import com.advice.schedule.setCurrentClock
import com.advice.schedule.toConference
import org.junit.Assert.*
import org.junit.Test

class DatabaseManagerTest {

    private val conferences: List<Conference> = listOf(
            FirebaseConference(id = 1, code = "abc", start_timestamp = fromString("2019-01-01"), end_timestamp = fromString("2019-01-01")),
            FirebaseConference(id = 2, code = "123", start_timestamp = fromString("2019-03-02"), end_timestamp = fromString("2019-03-03")),
            FirebaseConference(id = 3, code = "456", start_timestamp = fromString("2019-05-02"), end_timestamp = fromString("2019-05-03"))
    ).map { it.toConference() }

    @Test
    fun `get newest conference` (){
        setCurrentClock("2019-01-02T11:00:00.000-0000")

        val result = DatabaseManager.getNextConference(-1, conferences)

        assertEquals("123", result?.code)
    }

    @Test
    fun `get preferred conference` (){
        setCurrentClock("2019-01-01T11:00:00.000-0000")

        val result = DatabaseManager.getNextConference(2, conferences)

        assertEquals("123", result?.code)
    }

    @Test
    fun `get defcon conference` (){
        setCurrentClock("2019-01-01T11:00:00.000-0000")

        val defcon = FirebaseConference(
            id = 4,
            code = "DEFCON27",
            start_timestamp = fromString("2019-05-02"),
            end_timestamp = fromString("2019-05-03")
        ).toConference()

        val conferences = conferences + defcon

        val result = DatabaseManager.getNextConference(-1, conferences)

        assertEquals("DEFCON27", result?.code)
    }

    @Test
    fun `skip defcon conference when finished` (){
        setCurrentClock("2019-03-03T11:00:00.000-0000")

        val defcon = FirebaseConference(
            id = 4,
            code = "DEFCON27",
            start_timestamp = fromString("2019-02-02"),
            end_timestamp = fromString("2019-02-03")
        ).toConference()

        val conferences = conferences + defcon

        val result = DatabaseManager.getNextConference(-1, conferences)

        assertEquals("456", result?.code)
    }


    @Test
    fun `get newest conference when all finished` (){
        setCurrentClock("2019-06-01T11:00:00.000-0000")

        val result = DatabaseManager.getNextConference(-1, conferences)

        assertEquals("456", result?.code)
    }

    @Test
    fun `get newest conference when all finished with preference` (){
        setCurrentClock("2019-06-01T11:00:00.000-0000")

        val result = DatabaseManager.getNextConference(1, conferences)

        assertEquals("456", result?.code)
    }

}