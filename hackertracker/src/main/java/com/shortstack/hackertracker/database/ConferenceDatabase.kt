package com.shortstack.hackertracker.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.fromJsonFile
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.models.Conferences

/**
 * Created by Chris on 3/31/2018.
 */
@Database(entities = [(Conference::class)], version = 1)
@TypeConverters(value = [(Converters::class)])
abstract class ConferenceDatabase : RoomDatabase() {

    abstract fun conferenceDao(): ConferenceDao

    fun init() {
        val gson = App.application.gson

        conferenceDao().deleteAll()

        // Conferences
        gson.fromJsonFile(CONFERENCES_FILE, Conferences::class.java, root = "conferences").let {
            it.conferences.first().isSelected = true
            conferenceDao().insertAll(it.conferences)
        }
    }

    companion object {
        private const val CONFERENCES_FILE = "conferences.json"
    }


}