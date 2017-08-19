package com.shortstack.hackertracker.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.google.gson.Gson
import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.Model.Types
import com.shortstack.hackertracker.Network.SyncResponse

class TOORCONDatabaseController(context: Context, name: String = Constants.TOORCON_DATABASE_NAME, version: Int = 1) : DEFCONDatabaseController(context, name, version) {

    // Files
    private val PATCH_FILE = "patches.json"
    private val SCHEDULE_FILE = "schedule-full.json"
    private val TYPES_FILE = "event_type.json"

    // Tables
    private val SCHEDULE_TABLE_NAME = "Schedule"
    private val TYPES_TABLE_NAME = "Types"

    override fun initDatabase(db: SQLiteDatabase, gson: Gson) {
// Setting up databases
        var json = getJSONFromFile(PATCH_FILE)
        val patches = gson.fromJson(json, Patches::class.java)
        applyPatches(db, patches)

        // Schedule
        json = getJSONFromFile(SCHEDULE_FILE)
        val response = gson.fromJson(json, SyncResponse::class.java)
        initSchedule(db, response)

        initOtherDatabases(gson, db)
    }

    override fun updateDatabase(gson: Gson, db: SQLiteDatabase) {

    }

    override fun initOtherDatabases(gson: Gson, db: SQLiteDatabase) {
// Event Types
        var json = getJSONFromFile(TYPES_FILE)
        val types = gson.fromJson(json, Types::class.java)
        initTable(db, TYPES_TABLE_NAME, types.types)
    }
}