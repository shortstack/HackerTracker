package com.shortstack.hackertracker.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.Event.FavoriteEvent
import com.shortstack.hackertracker.Model.*
import com.shortstack.hackertracker.Network.SyncResponse
import com.shortstack.hackertracker.format8601
import io.reactivex.Observable
import java.util.*

open class DEFCONDatabaseController(context: Context, name: String = Constants.DEFCON_DATABASE_NAME, version: Int = 1) : DatabaseController(context, name, version) {

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
    val KEY_INDEX_SPEAKER = "indexsp"


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

    fun initSchedule(database: SQLiteDatabase = writableDatabase, response: SyncResponse) {

        App.application.storage.lastUpdated = response.updatedDate
        App.application.storage.lastSyncVersion = BuildConfig.VERSION_CODE

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

    override fun updateDatabase(gson: Gson, db: SQLiteDatabase) {
        // Schedule
        var json = getJSONFromFile(SCHEDULE_FILE)
        val response = gson.fromJson(json, SyncResponse::class.java)
        updateSchedule(db, response)

        initOtherDatabases(gson, db)
    }

    fun updateSchedule(database: SQLiteDatabase = writableDatabase, response: SyncResponse): Int {
        App.application.storage.lastUpdated = response.updatedDate
        App.application.storage.lastSyncVersion = BuildConfig.VERSION_CODE

        val schedule = response.schedule
        var count = 0

        for (item in schedule) {
            if (updateScheduleItem(database, item)) {
                count++
            }
        }

        return count
    }

    override fun initOtherDatabases(gson: Gson, db: SQLiteDatabase) {
        // Vendors
        var json = getJSONFromFile(VENDORS_FILE)
        val vendors = gson.fromJson(json, Vendors::class.java)
        initTable(db, VENDORS_TABLE_NAME, vendors.vendors)

        // Event Types
        json = getJSONFromFile(TYPES_FILE)
        val types = gson.fromJson(json, Types::class.java)
        initTable(db, TYPES_TABLE_NAME, types.types)

        json = getJSONFromFile(SPEAKERS_FILE)
        val speakers = gson.fromJson(json, Speakers::class.java)
        initTable(db, SPEAKERS_TABLE_NAME, speakers.speakers)
    }

    private val recentLimit = 25

    fun getRecentUpdates(): List<Item> {
        return query(SCHEDULE_TABLE_NAME, Item::class.java, orderBy = "updated_at DESC", limit = recentLimit)
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


    private val SCHEDULE_PAGE_SIZE = 10
    fun getRecent() :Observable<List<Item>> {
        return Observable.create {
            subscriber ->
            val list = getRecentUpdates()
            subscriber.onNext(list)
            subscriber.onComplete()
        }
    }


    fun getItems(vararg type: String, page: Int = 0) : Observable<List<Item>> {
        return Observable.create {
            subscriber ->
            val list = getItemByDate(*type, page = page)
            subscriber.onNext(list)
            subscriber.onComplete()
        }
    }


    @Throws(SQLiteException::class)
    fun getItemByDate(vararg type: String, page: Int = 0): List<Item> {



        val args = ArrayList(Arrays.asList(*type))

        // Types
        var selection: String = ""

        if (type.isNotEmpty()) {
            val array = Array(type.size, { "$KEY_TYPE = ?" })
            selection = array.joinToString(prefix = "(", separator = " OR ", postfix = ")")
        }

        // Date
        if (App.application.storage.showActiveEventsOnly()) {
            val currentDate = App.getCurrentCalendar()

            if (selection.isNotEmpty())
                selection += "AND "

            selection += "( $KEY_END_DATE > ? )"

            args.add(currentDate.format8601())
        }



        return query(SCHEDULE_TABLE_NAME, Item::class.java,
                selection = selection, selectionArgs = args.toTypedArray(),
                orderBy = KEY_START_DATE, limit = SCHEDULE_PAGE_SIZE, page = page)
    }

    fun getSpeaker(speaker: Speaker): Speaker {
        val query = query(SPEAKERS_TABLE_NAME, Speaker::class.java, selection = "$KEY_INDEX_SPEAKER = ?", selectionArgs = arrayOf(speaker.id.toString()))
        if( query.size == 0 ) {
            Logger.e("Could not find speaker by id. $speaker")
            return speaker
        }
        return query.first()
    }

    val types
        get() = query(TYPES_TABLE_NAME, Types.Type::class.java)

    val speakers
        get() = query(SPEAKERS_TABLE_NAME, Speaker::class.java)

    val vendors
        get() = query(VENDORS_TABLE_NAME, Vendors.Vendor::class.java)

}