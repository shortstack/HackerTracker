package com.shortstack.hackertracker.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.fromJsonFile
import com.shortstack.hackertracker.models.Vendor
import com.shortstack.hackertracker.models.response.Vendors

/**
 * Created by Chris on 3/31/2018.
 */
@Database(entities = [(Vendor::class)], version = 1)
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun vendorDao(): VendorDao

    fun init() {
        val gson = App.application.gson

        val vendors = gson.fromJsonFile(VENDORS_FILE, Vendors::class.java)
        vendorDao().insertAll(vendors.vendors.toList())
    }


    companion object {
        private const val SCHEDULE_FILE = "schedule-full.json"
        private const val TYPES_FILE = "event_type.json"
        private const val SPEAKERS_FILE = "speakers.json"
        private const val VENDORS_FILE = "vendors.json"
    }
}