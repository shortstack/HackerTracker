package com.shortstack.hackertracker.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
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

    val SCHEDULE = "database_2.db"
    val VENDORS = "vendors.sqlite"


    val SCHEDULE_VERSION = 210
    val VENDOR_VERSION = 14
    val SCHEDULE_TABLE_NAME = "schedule"
    val SPEAKERS_TABLE_NAME = "speakers"

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
        setScheduleBookmarked(value, item.id)

        item.toggleBookmark()
        App.application.postBusEvent(FavoriteEvent(item.id))

        if (item.isBookmarked) {
            App.application.notificationHelper.scheduleItemNotification(item)
        }
    }

    private fun setScheduleBookmarked(state: Int, id: Int) {
        schedule.execSQL("UPDATE data SET starred=$state WHERE id=$id")
    }

    fun addScheduleItem(item: Item) {
        schedule.insert(SCHEDULE_TABLE_NAME, null, item.getContentValues(mGson))

        Logger.d("Inserted item.")

        val cursor = schedule.rawQuery("SELECT * FROM data", arrayOf<String>())
        val count = cursor.count
        Logger.d("Count now: " + count)


        cursor.moveToFirst()

        do {

        } while (cursor.moveToNext())

        cursor.close()
    }

    fun updateScheduleItem(item: Item) {
        val filter = "id=?"
        val args = arrayOf(item.id.toString())

        val schedule = App.application.databaseController.schedule
        val values = item.getContentValues(mGson)

        values.remove("who")
        values.remove("location")
        values.remove("index")

        values.put("updated_at", System.currentTimeMillis())

        Logger.d("Updating item: " + values)

        val rowsUpdated = schedule.update(SCHEDULE_TABLE_NAME, values, filter, args)
        if (rowsUpdated == 0) {
            schedule.insert(SCHEDULE_TABLE_NAME, null, values)
            // Change to insert new event.
            //            App.Companion.getApplication().postBusEvent(new UpdateListContentsEvent());
        } else {
            // Change to update event.
            //            App.Companion.getApplication().postBusEvent(new UpdateListContentsEvent());

            val item1 = getScheduleItemFromId(item.id)

            if (item1.isBookmarked) {

                val notificationHelper = App.application.notificationHelper
                // Cancel the notification, in case the time changes.
                notificationHelper.cancelNotification(item.id)

                // Set a new one.
                notificationHelper.scheduleItemNotification(item)

                notificationHelper.postNotification(notificationHelper.getUpdatedItemNotification(item), item.id)
            }
        }
    }

    fun updateSchedule(items: List<Item>) {
        for (item in items) {
            updateScheduleItem(item)
        }
    }

    fun getScheduleItemFromId(id: Int): Item {
        val cursor = schedule.query(SCHEDULE_TABLE_NAME, null, "id=?", arrayOf(id.toString()), null, null, null, null)

        if (cursor.moveToFirst()) {
            val item = Item.CursorToItem(mGson, cursor)
            cursor.close()

            return item
        }

        return Item()
    }

    @Throws(SQLiteException::class)
    fun getItemByDate(vararg type: String): List<Item> {
        val result = ArrayList<Item>()
        val args = ArrayList(Arrays.asList(*type))


        var time: Long


        // Types
        var selection = "( entry_type=?"
        for (i in 0..type.size - 1 - 1) {
            selection += " OR entry_type=?"

        }
        selection += " ) "

        // Date
        if (App.storage!!.showActiveEventsOnly()) {


            val currentDate = App.getCurrentCalendar()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val timeFormat = SimpleDateFormat("hh:mm")

            selection += "AND ( end_date > ? )"

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

                        val item = Company()

                        item.id = cursor.getInt(cursor.getColumnIndex("id"))
                        item.title = cursor.getString(cursor.getColumnIndex("title"))
                        item.description = cursor.getString(cursor.getColumnIndex("description"))
                        item.link = cursor.getString(cursor.getColumnIndex("link"))
                        item.partner = cursor.getInt(cursor.getColumnIndex("partner"))

                        result.add(item)
                    } while (cursor.moveToNext())
                }
            } finally {
                cursor.close()
            }

            return result
        }
}
