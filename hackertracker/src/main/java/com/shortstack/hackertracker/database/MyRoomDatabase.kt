package com.shortstack.hackertracker.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.fromJsonFile
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.models.response.Speakers
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors

/**
 * Created by Chris on 3/31/2018.
 */
@Database(entities = [(Event::class), (Type::class), (Vendor::class), (Speaker::class)], version = 1)
@TypeConverters(value = [(Converters::class)])
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    abstract fun typeDao(): TypeDao

    abstract fun speakerDao(): SpeakerDao

    abstract fun vendorDao(): VendorDao

    fun init() {
        val gson = App.application.gson

        // Types
        gson.fromJsonFile(TYPES_FILE, Types::class.java).let {
            typeDao().insertAll(it.types)
        }

        // Schedule
        gson.fromJsonFile(SCHEDULE_FILE, Events::class.java).let {
            eventDao().insertAll(it.events)
        }

        // Vendors
        gson.fromJsonFile(VENDORS_FILE, Vendors::class.java).let {
            vendorDao().insertAll(it.vendors)
        }

        // Speakers
        gson.fromJsonFile(SPEAKERS_FILE, Speakers::class.java).let {
            speakerDao().insertAll(it.speakers)
        }
    }


    companion object {
        private const val SCHEDULE_FILE = "schedule-full.json"
        private const val TYPES_FILE = "event_type.json"
        private const val SPEAKERS_FILE = "speakers.json"
        private const val VENDORS_FILE = "vendors.json"
    }
}