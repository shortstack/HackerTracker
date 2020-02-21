package com.shortstack.hackertracker.database

import com.shortstack.hackertracker.models.firebase.FirebaseConference
import com.shortstack.hackertracker.models.local.Conference
import com.shortstack.hackertracker.setCurrentClock
import com.shortstack.hackertracker.toConference
import org.junit.Assert.*
import org.junit.Test

class DatabaseManagerTest {

    private val conferences: List<Conference> = listOf(
            FirebaseConference(code = "abc", start_date = "2019-01-01", end_date = "2019-01-01"),
            FirebaseConference(code = "123", start_date = "2019-03-02", end_date = "2019-03-03"),
            FirebaseConference(code = "456", start_date = "2019-05-02", end_date = "2019-05-03")
    ).map { it.toConference() }

    @Test
    fun `get newest conference` (){
        setCurrentClock("2019-01-02T11:00:00.000-0000")

        val result = DatabaseManager.getNextConference(conferences)

        assertEquals("123", result?.code)
    }

    @Test
    fun `get newest conference when all finished` (){
        setCurrentClock("2019-06-01T11:00:00.000-0000")

        val result = DatabaseManager.getNextConference(conferences)

        assertEquals("456", result?.code)
    }

}