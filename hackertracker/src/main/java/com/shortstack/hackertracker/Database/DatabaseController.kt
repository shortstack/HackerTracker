package com.shortstack.hackertracker.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.crashlytics.android.Crashlytics.getInstance
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.Event.FavoriteEvent
import com.shortstack.hackertracker.Model.Company
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Model.Speaker
import java.text.SimpleDateFormat
import java.util.*

class DatabaseController(context: Context) {

    val MAX_RECENT_UPDATES = 25

    val SCHEDULE = "database.db"
    val VENDORS = "vendors.sqlite"


    val SCHEDULE_VERSION = 212
    val VENDOR_VERSION = 14
    val SCHEDULE_TABLE_NAME = "schedule"
    val SPEAKERS_TABLE_NAME = "speakers"

    val KEY_INDEX = "`index`"
    val KEY_BOOKMARKED = "bookmarked"
    val KEY_TYPE = "entry_type"
    val KEY_END_DATE = "end_date"

    val SELECT_ALL_FROM = "SELECT * from "

    val schedule: SQLiteDatabase
    val mVendors: SQLiteDatabase

    val mGson: Gson

    init {
        var databaseAdapter: DatabaseHelper = DatabaseHelper(context, SCHEDULE, SCHEDULE_VERSION)

        schedule = databaseAdapter.writableDatabase

        databaseAdapter = DatabaseHelper(context, VENDORS, VENDOR_VERSION)
        mVendors = databaseAdapter.writableDatabase

        mGson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    }


    protected fun finalize() {
        schedule.close()
        mVendors.close()
    }

    fun toggleBookmark(item: Item) {
        val value = if (item.isBookmarked) Constants.UNBOOKMARKED else Constants.BOOKMARKED
        setScheduleBookmarked(value, item.index)

        item.toggleBookmark()
        App.application.postBusEvent(FavoriteEvent(item.index))

        if (item.isBookmarked) {
            App.application.notificationHelper.scheduleItemNotification(item)
        }
    }

    private fun setScheduleBookmarked(state: Int, id: Int) {
        schedule.execSQL("UPDATE $SCHEDULE_TABLE_NAME SET $KEY_BOOKMARKED=$state WHERE $KEY_INDEX=$id")
    }

    fun addScheduleItem(item: Item) {
        schedule.insert(SCHEDULE_TABLE_NAME, null, item.getContentValues(mGson))

        Logger.d("Inserted item.")

        val cursor = schedule.rawQuery(SELECT_ALL_FROM + SCHEDULE_TABLE_NAME, arrayOf<String>())
        val count = cursor.count
        Logger.d("Count now: " + count)


        cursor.moveToFirst()

        do {

        } while (cursor.moveToNext())

        cursor.close()
    }

    fun updateScheduleItem(item: Item): Boolean {

        val filter = "$KEY_INDEX=?"
        val args = arrayOf(item.index.toString())

        val schedule = App.application.databaseController.schedule
        val values = item.getContentValues(mGson)

        values.remove("index")
        values.remove("bookmarked")

        val existing = getScheduleItemFromId(item.index)

        if (existing?.updatedAt.equals(item.updatedAt)) {
            return false
        }

        val rowsUpdated = schedule.update(SCHEDULE_TABLE_NAME, values, filter, args)
        if (rowsUpdated == 0) {
            // New event.
            schedule.insert(SCHEDULE_TABLE_NAME, null, values)
        } else {
            // Updated event.
            val item1 = getScheduleItemFromId(item.index) ?: return false

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

    fun updateSchedule(items: Array<Item>) {
        val recent = App.storage.recentUpdates

        for (item in items) {
            if ( updateScheduleItem(item) ) {
                recent.add(item.index.toString())

                if( recent.size > MAX_RECENT_UPDATES ) {
                    recent.remove(recent.iterator().next())
                }
            }
        }

        App.storage.recentUpdates = recent
    }

    fun getScheduleItemFromId(id: Int): Item? {
        val cursor = schedule.query(SCHEDULE_TABLE_NAME, null, "$KEY_INDEX=?", arrayOf(id.toString()), null, null, null, null)

        if (cursor.moveToFirst()) {
            val item = Item.CursorToItem(mGson, cursor)
            cursor.close()

            return item
        }

        return null
    }

    @Throws(SQLiteException::class)
    fun getItemByDate(vararg type: String): List<Item> {
        val result = ArrayList<Item>()
        val args = ArrayList(Arrays.asList(*type))


        var time: Long


        // Types
        var selection = "( $KEY_TYPE=?"
        for (i in 0..type.size - 1 - 1) {
            selection += " OR $KEY_TYPE=?"
        }
        selection += " ) "

        // Date
        if (App.storage.showActiveEventsOnly()) {


            val currentDate = App.getCurrentCalendar()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val timeFormat = SimpleDateFormat("hh:mm")

            selection += "AND ( $KEY_END_DATE > ? )"

            args.add(timeFormat.format(currentDate.time))
        }


        // Debugging
        var args_print = ""
        for (arg in args) {
            args_print += (arg + ", ")
        }

        Logger.d("Selection: $selection Args: $args_print")
        time = System.currentTimeMillis()


        // Query
        val cursor = schedule.query(SCHEDULE_TABLE_NAME, null, selection, args.toTypedArray(), null, null, "start_date")

//        Logger.d("Cursor: " + cursor.count + " Took: " + (System.currentTimeMillis() - time))


        time = System.currentTimeMillis()

        // Adding to list
        if (cursor.moveToFirst()) {

            do {
                result.add(Item.CursorToItem(mGson, cursor))
            } while (cursor.moveToNext())

        }
        cursor.close()

        Logger.d("Size of Results: " + result.size + " Took: " + (System.currentTimeMillis() - time))

        return result
    }

    fun getSpeakers(): List<Speaker> {

        val result = ArrayList<Speaker>()

        val cursor = schedule.query(SPEAKERS_TABLE_NAME, null, null, null, null, null, null)


        if (cursor.moveToFirst()) {

            do {
                result.add(Speaker.CursorToItem(mGson, cursor))
            } while (cursor.moveToNext())

        }

        cursor.close()

        return result

    }


    private fun getQueryString(type: Array<String>): String {
        var query = "SELECT * FROM data WHERE"
        if (type.isNotEmpty()) {
            query += " ("
            for (i in type.indices) {
                query += "type=?"
                if (i < type.size - 1) query += " OR "
            }
            query += ") OR"
        }
        query += " starred=1 ORDER BY date, begin"
        return query
    }

    val vendors: List<Company>
        get() {
            val result = ArrayList<Company>()

            val cursor = mVendors.rawQuery("SELECT * FROM data", arrayOf<String>())

            try {
                if (cursor.moveToFirst()) {
                    do {

                        val item = Company.CursorToCompany(mGson, cursor)

                        result.add(item)
                    } while (cursor.moveToNext())
                }
            } catch (ex: IllegalStateException) {
                val core = getInstance().core
                if (core != null) {
                    core.log("IllegalStateException in getVendors!")
                    core.log("Results is now: " + result.size)
                    if (result.size > 0) {
                        core.log("Last result: " + result.last().title)
                    }

                    core.logException(ex)
                }
            } finally {
                cursor.close()
            }

            return result
        }
}
