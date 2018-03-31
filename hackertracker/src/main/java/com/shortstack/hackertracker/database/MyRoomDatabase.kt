package com.shortstack.hackertracker.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.fromJsonFile
import com.shortstack.hackertracker.models.Type
import com.shortstack.hackertracker.models.Vendor
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors

/**
 * Created by Chris on 3/31/2018.
 */
@Database(entities = [(Type::class), (Vendor::class)], version = 2)
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun typeDao(): TypeDao

    abstract fun vendorDao(): VendorDao

    fun init() {
        val gson = App.application.gson

        // Types
        gson.fromJsonFile(TYPES_FILE, Types::class.java).let {
            typeDao().insertAll(it.types.toList())
        }

        // Vendors
        gson.fromJsonFile(VENDORS_FILE, Vendors::class.java).let {
            vendorDao().insertAll(it.vendors.toList())
        }

    }


    companion object {
        private const val SCHEDULE_FILE = "schedule-full.json"
        private const val TYPES_FILE = "event_type.json"
        private const val SPEAKERS_FILE = "speakers.json"
        private const val VENDORS_FILE = "vendors.json"
    }
}