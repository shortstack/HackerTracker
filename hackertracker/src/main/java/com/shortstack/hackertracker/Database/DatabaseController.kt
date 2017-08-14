package com.shortstack.hackertracker.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.Event.FavoriteEvent
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Model.Speakers
import com.shortstack.hackertracker.Model.Types
import com.shortstack.hackertracker.Model.Vendors
import com.shortstack.hackertracker.Network.SyncResponse
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DatabaseController(private val context: Context, name: String = Constants.DEFCON_DATABASE_NAME, version: Int = 1) : SQLiteOpenHelper(context, name, null, version) {

    // Files
    private val PATCH_FILE = "patches.json"
    private val SCHEDULE_FILE = "schedule-full.json"
    private val LOCATIONS_FILE = "locations.json"
    private val TYPES_FILE = "event_type.json"
    private val SPEAKERS_FILE = "speakers.json"
    private val VENDORS_FILE = "vendors.json"

    // Tables
    private val SCHEDULE_TABLE_NAME = "Schedule"
    private val LOCATIONS_TABLE_NAME = "Locations"
    private val TYPES_TABLE_NAME = "Types"
    private val SPEAKERS_TABLE_NAME = "Speakers"
    private val VENDORS_TABLE_NAME = "Vendors"

    // Keys
    val KEY_INDEX = "`index`"
    val KEY_BOOKMARKED = "bookmarked"
    val KEY_TYPE = "entry_type"
    val KEY_END_DATE = "end_date"
    val KEY_START_DATE = "start_date"

    // SQL
    val SELECT_ALL_FROM = "SELECT * from "

    init {
//         This will call onCreate if the database is new.
        val db = writableDatabase

        if (isScheduleOutOfDate()) {
            Logger.d("Have not synced the current version. Update the database with the json.")
            updateDataSet(db)
        }
    }


    private fun isScheduleOutOfDate() = App.storage.lastSyncVersion != BuildConfig.VERSION_CODE /*|| BuildConfig.DEBUG*/

    protected fun finalize() {
        close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        val gson = App.application.gson

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

    private fun initOtherDatabases(gson: Gson, db: SQLiteDatabase) {
        // Vendors
        var json = getJSONFromFile(VENDORS_FILE)
        val vendors = gson.fromJson(json, Vendors::class.java)
        initVendors(db, vendors)

        // Event Types
        json = getJSONFromFile(TYPES_FILE)
        val types = gson.fromJson(json, Types::class.java)
        initTypes(db, types)

        json = getJSONFromFile(SPEAKERS_FILE)
        val speakers = gson.fromJson(json, Speakers::class.java)
        initSpeakers(db, speakers)
    }

    fun updateDataSet(db: SQLiteDatabase) {
        val gson = App.application.gson

        // Schedule
        var json = getJSONFromFile(SCHEDULE_FILE)
        val response = gson.fromJson(json, SyncResponse::class.java)
        updateSchedule(db, response)

        initOtherDatabases(gson, db)
    }

    private fun applyPatches(database: SQLiteDatabase, patches: Patches) {
        patches.patches.indices
                .map { patches.patches[it] }
                .forEach {
                    for (command in it.commands) {
                        database.execSQL(command)
                    }
                }
    }

    private fun initVendors(database: SQLiteDatabase, vendors: Vendors?) {
        database.beginTransaction()

        // Clearing out the table.
        database.delete(VENDORS_TABLE_NAME, null, null)

        try {

            vendors?.vendors?.forEach {
                val values = it.getContentValues(App.application.gson)
                values.remove("index")
                database.insert(VENDORS_TABLE_NAME, null, values)
            }

            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
    }

    private fun initTypes(database: SQLiteDatabase, types: Types) {
        database.beginTransaction()

        // Clearing out the table.
        database.delete(TYPES_TABLE_NAME, null, null)

        try {

            types.types.forEach {
                val values = getContentValues(it, App.application.gson)
                database.insert(TYPES_TABLE_NAME, null, values)
            }

            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }

    }

    private fun initSpeakers(database: SQLiteDatabase, speakers: Speakers) {
        database.beginTransaction()

        // Clearing out the table.
        database.delete(SPEAKERS_TABLE_NAME, null, null)

        try {

            speakers.speakers.forEach {
                val values = getContentValues(it, App.application.gson)
                database.insert(SPEAKERS_TABLE_NAME, null, values)
            }

            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
    }

    fun updateSchedule(database: SQLiteDatabase = writableDatabase, response: SyncResponse): Int {
        App.storage.lastUpdated = response.updatedDate
        App.storage.lastSyncVersion = BuildConfig.VERSION_CODE

        val schedule = response.schedule
        var count = 0

        for (item in schedule) {
            if (updateScheduleItem(database, item)) {
                count++
            }
        }

        return count
    }

    fun initSchedule(database: SQLiteDatabase = writableDatabase, response: SyncResponse) {

        App.storage.lastUpdated = response.updatedDate
        App.storage.lastSyncVersion = BuildConfig.VERSION_CODE

        val schedule = response.schedule
        val gson = App.application.gson

        database.beginTransaction()

        try {
            schedule.forEach {
                val values = it.getContentValues(gson)
                values.put(KEY_INDEX, values.getAsInteger("index"))
                values.remove("index")

                database.insert(SCHEDULE_TABLE_NAME, null, values)
            }
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
    }

    fun getRecentUpdates(): List<Item> {
        var result = ArrayList<Item>()

        val cursor = readableDatabase.query(false, SCHEDULE_TABLE_NAME, null, null, null, null, null, "updated_at DESC", "25")

        if (cursor.moveToFirst()) {
            do {
                result.add(Item.CursorToItem(App.application.gson, cursor))
            } while (cursor.moveToNext())
        }


        return result
    }


    fun updateScheduleItem(db: SQLiteDatabase, item: Item): Boolean {
        val filter = "$KEY_INDEX=?"
        val args = arrayOf(item.index.toString())

        val values = item.getContentValues(App.application.gson)


        values.put(KEY_INDEX, values.getAsInteger("index"))
        values.remove("index")
        values.remove("bookmarked")


        val existing = getScheduleItemFromId(db, item.index)

        if (existing?.updatedAt.equals(item.updatedAt)) {
            return false
        }

        val rowsUpdated = db.update(SCHEDULE_TABLE_NAME, values, filter, args)
        if (rowsUpdated == 0) {
            // New event.
            db.insert(SCHEDULE_TABLE_NAME, null, values)
        } else {
            // Updated event.
            val item1 = getScheduleItemFromId(db, item.index) ?: return false

            if (item1.isBookmarked) {

                val notificationHelper = App.application.notificationHelper
                // Cancel the notification, in case the time changes.
                notificationHelper.cancelNotification(item.index)

                // Set a new one.
                notificationHelper.scheduleItemNotification(item)

                // If bookmarked, throw up a notification.
                notificationHelper.postNotification(notificationHelper.getUpdatedItemNotification(item), item.index)
            }
        }

        return true
    }

    fun getScheduleItemFromId(db: SQLiteDatabase = readableDatabase, id: Int): Item? {
        val cursor = db.rawQuery("$SELECT_ALL_FROM $SCHEDULE_TABLE_NAME WHERE $KEY_INDEX = $id", arrayOf<String>())

        if (cursor.moveToFirst()) {
            val item = Item.CursorToItem(App.application.gson, cursor)
            cursor.close()
            return item
        }

        return null
    }

    fun toggleBookmark(db: SQLiteDatabase = writableDatabase, item: Item) {
        val value = if (item.isBookmarked) Constants.UNBOOKMARKED else Constants.BOOKMARKED
        setScheduleBookmarked(db, value, item.index)

        item.toggleBookmark()
        App.application.postBusEvent(FavoriteEvent(item.index))

        if (item.isBookmarked) {
            App.application.notificationHelper.scheduleItemNotification(item)
        }
    }

    private fun setScheduleBookmarked(db: SQLiteDatabase = writableDatabase, state: Int, id: Int) {
        db.execSQL("UPDATE $SCHEDULE_TABLE_NAME SET $KEY_BOOKMARKED=$state WHERE $KEY_INDEX=$id")
    }

    private fun getJSONFromFile(filename: String): String {
        try {
            val `is` = context.assets.open(filename)
            val size = `is`.available()

            val buffer = ByteArray(size)

            `is`.read(buffer)
            `is`.close()

            return String(buffer)

        } catch (e: IOException) {
            Logger.e(e, "Could not create the database.")
        }

        return ""
    }


    @Throws(SQLiteException::class)
    fun getItemByDate(vararg type: String): List<Item> {
        val result = ArrayList<Item>()
        val args = ArrayList(Arrays.asList(*type))

        // Types
        var selection: String = ""

        if (type.isNotEmpty()) {
            selection = "( $KEY_TYPE=?"
            for (i in 0..type.size - 1 - 1) {
                selection += " OR $KEY_TYPE=?"
            }
            selection += " ) "
        }

        // Date
        if (App.storage.showActiveEventsOnly()) {
            val currentDate = App.getCurrentCalendar()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

            if (selection.isNotEmpty())
                selection += "AND "

            selection += "( $KEY_END_DATE > ? )"

            args.add(dateFormat.format(currentDate.time))
        }


        // Query
        val cursor = readableDatabase.query(SCHEDULE_TABLE_NAME, null, selection, args.toTypedArray(), null, null, KEY_START_DATE)

//        Logger.d("Selection: $selection Args: $args returns " + cursor.count + " rows.")


        // Adding to list
        if (cursor.moveToFirst()) {

            do {
                result.add(Item.CursorToItem(App.application.gson, cursor))
            } while (cursor.moveToNext())

        }
        cursor.close()

        return result
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Logger.d("Updating database.")
    }

    val types: List<Types.Type>
        get() {
            val result = ArrayList<Types.Type>()

            val cursor = readableDatabase.rawQuery("$SELECT_ALL_FROM $TYPES_TABLE_NAME", arrayOf<String>())

            try {
                if (cursor.moveToFirst()) {
                    do {
                        result.add(fromCursor(Types.Type::class.java, cursor))
                    } while (cursor.moveToNext())
                }
            } catch (ex: IllegalStateException) {
                Logger.e(ex, "Could not fetch types.")
                Crashlytics.getInstance().core.logException(ex)
            } finally {
                cursor.close()
            }

            return result
        }

    val vendors: List<Vendors.Vendor>
        get() {
            val result = ArrayList<Vendors.Vendor>()

            val cursor = readableDatabase.rawQuery("$SELECT_ALL_FROM $VENDORS_TABLE_NAME", arrayOf<String>())

            try {
                if (cursor.moveToFirst()) {
                    do {
                        result.add(fromCursor(Vendors.Vendor::class.java, cursor))
                    } while (cursor.moveToNext())
                }
            } catch (ex: IllegalStateException) {
                Logger.e(ex, "Could not fetch vendors.")
                Crashlytics.getInstance().core.logException(ex)
            } finally {
                cursor.close()
            }

            return result
        }

    val speakers: List<Speakers.Speaker>
        get() {
            val result = ArrayList<Speakers.Speaker>()
            val cursor = readableDatabase.rawQuery("$SELECT_ALL_FROM $SPEAKERS_TABLE_NAME", arrayOf<String>())
            if (cursor.moveToFirst()) {
                do {
                    result.add(fromCursor(Speakers.Speaker::class.java, cursor))
                } while (cursor.moveToNext())
            }

            cursor.close()
            return result
        }

    companion object {
        fun exists(context: Context): Boolean {
            val dbFile = context.getDatabasePath(Constants.DEFCON_DATABASE_NAME)
            return dbFile.exists()
        }

        fun <T> fromCursor(t: Class<T>, cursor: Cursor): T {
            val obj = getJsonObject(cursor)
            return App.application.gson.fromJson(obj.toString(), t)
        }

        private fun getJsonObject(cursor: Cursor): JSONObject {
            val obj = JSONObject()

            val totalColumn = cursor.columnCount

            for (i in 0..totalColumn - 1) {
                try {
                    obj.put(cursor.getColumnName(i), cursor.getString(i))
                } catch (e: Exception) {
                    Logger.e(e, "Failed to convert Cursor into JSONObject.")
                }
            }
            return obj
        }

        fun <T> getContentValues(t: T, gson: Gson): ContentValues {
            val values = ContentValues()

            val json = gson.toJson(t)
            try {
                val obj = JSONObject(json)

                val keys = obj.keys()
                var key: String
                while (keys.hasNext()) {
                    key = keys.next()
                    values.put(key, obj.getString(key))
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return values
        }
    }

    fun getSpeaker(speaker: Speakers.Speaker): Speakers.Speaker {
        val cursor = readableDatabase.query(SPEAKERS_TABLE_NAME, null, "indexsp = ?", arrayOf(speaker.id.toString()), null, null, null)
        if (cursor.count == 0)
            return speaker
        if( !cursor.moveToFirst() )
             return speaker
        return fromCursor(Speakers.Speaker::class.java, cursor)
    }
}
