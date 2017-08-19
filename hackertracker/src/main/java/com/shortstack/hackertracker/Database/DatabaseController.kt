package com.shortstack.hackertracker.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.Model.Item
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

abstract class DatabaseController(protected val context: Context, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {


    // SQL
    val SELECT_ALL_FROM = "SELECT * from "

    fun checkDatabase() {
        val db = writableDatabase

        if (isScheduleOutOfDate()) {
            Logger.d("Have not synced the current version. Update the database with the json.")
            updateDataSet(db)
        }
    }

    fun exists() = DatabaseController.exists(context, databaseName)

    private fun isScheduleOutOfDate() = App.application.storage.lastSyncVersion != BuildConfig.VERSION_CODE/* || BuildConfig.DEBUG*/

    protected fun finalize() {
        close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        val gson = App.application.gson
        initDatabase(db, gson)
    }

    abstract protected fun initDatabase(db: SQLiteDatabase, gson: Gson)
    abstract protected fun initOtherDatabases(gson: Gson, db: SQLiteDatabase)
    abstract protected fun updateDatabase(gson: Gson, db: SQLiteDatabase)

    fun updateDataSet(db: SQLiteDatabase) {
        val gson = App.application.gson
        updateDatabase(gson, db)
    }


    protected fun applyPatches(database: SQLiteDatabase, patches: Patches) {
        patches.patches.indices
                .map { patches.patches[it] }
                .forEach {
                    for (command in it.commands) {
                        database.execSQL(command)
                    }
                }
    }

    protected fun <T> initTable(database: SQLiteDatabase, table: String, array: Array<T>) {
        database.beginTransaction()

        // Clearing out the table.
        database.delete(table, null, null)
        try {
            array.forEach {
                val values = getContentValues(it, App.application.gson)
                database.insert(table, null, values)
            }
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
    }

    protected fun <T> query(table: String, t: Class<T>,
                            columns: Array<String>? = null, selection: String? = null, selectionArgs: Array<String>? = null,
                            groupBy: String? = null, having: String? = null, orderBy: String? = null, limit: Int? = null, page: Int? = null
    ): ArrayList<T> {
        val result = ArrayList<T>()
        var limitOffset: String? = null

        if (page != null && limit != null ) {
            limitOffset = "${page * limit}, $limit"
        }

        val cursor = readableDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limitOffset)

        if (cursor.moveToFirst()) {
            do {
                result.add(fromCursor(t, cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()

        return result
    }


    protected fun getJSONFromFile(filename: String): String {
        try {
            val stream = context.assets.open("$databaseName/$filename")

            val size = stream.available()

            val buffer = ByteArray(size)

            stream.read(buffer)
            stream.close()

            return String(buffer)

        } catch (e: IOException) {
            Logger.e(e, "Could not create the database.")
        }

        return ""
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO()
    }


    companion object {
        fun exists(context: Context, database: String): Boolean {
            val dbFile = context.getDatabasePath(database)
            return dbFile.exists()
        }

        fun <T> fromCursor(t: Class<T>, cursor: Cursor): T {
            val gson = App.application.gson
            val obj = getJsonObject(cursor)

            if (t == Item::class.java) {
                val who = obj.getString("who")
                obj.remove("who")
                obj.put("who", JSONArray(who))
            }




            return gson.fromJson(obj.toString(), t)
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


}
