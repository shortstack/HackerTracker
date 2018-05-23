package com.shortstack.hackertracker.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.models.Item
import com.shortstack.hackertracker.joinSQLOr
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

abstract class DatabaseController(protected val context: Context, protected val storage: SharedPreferencesUtil, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {


    val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    // SQL
    val SELECT_ALL_FROM = "SELECT * from "
    val LIKE = "LIKE ?"
    val DATABASE_DIRECTORY = "database"

    fun checkDatabase() {
        val db = writableDatabase

        if (isScheduleOutOfDate()) {
            Logger.d("Have not synced the current version. Update the database with the json.")
            updateDataSet(db)
        }
    }

    fun exists() = DatabaseController.exists(context, databaseName)

    private fun isScheduleOutOfDate() = storage.lastSyncVersion != BuildConfig.VERSION_CODE/* || BuildConfig.DEBUG*/

    protected fun finalize() {
        close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        initDatabase(db, gson)
    }

    abstract protected fun initDatabase(db: SQLiteDatabase, gson: Gson)
    abstract protected fun initOtherDatabases(gson: Gson, db: SQLiteDatabase)
    abstract protected fun updateDatabase(gson: Gson, db: SQLiteDatabase)

    fun updateDataSet(db: SQLiteDatabase) {
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
                val values = getContentValues(it, gson)
                database.insert(table, null, values)
            }
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
    }

    fun <T> query(table: String, t: Class<T>,
                  columns: Array<String>? = null, selection: String? = null, selectionArgs: MutableList<String>? = null,
                  groupBy: String? = null, having: String? = null, orderBy: String? = null, limit: Int? = null, page: Int = 0,
                  searchColumns: MutableList<String>? = null, searchText: String? = null
    ): ArrayList<T> {
        val result = ArrayList<T>()
        var limitOffset: String? = null
        var finalSelection = selection
        var finalSelectArgs = selectionArgs

        // Limit and offset
        if (limit != null) {
            limitOffset = "${page * limit}, $limit"
        }


        // Search by text
        if (searchText != null) {

            val columns = getSearchColumns(searchColumns!!)?.joinSQLOr()

            if( finalSelection == null ) {
                finalSelection = columns
            } else {
                finalSelection += columns
            }

            val array = Array<String>(searchColumns.size, { "%$searchText%" })

            if (finalSelectArgs == null)
                finalSelectArgs = array.toMutableList()
            else
                finalSelectArgs.addAll(array)
        }


        val cursor = readableDatabase.query(table, columns, finalSelection, finalSelectArgs?.toTypedArray(), groupBy, having, orderBy, limitOffset)

        if (cursor.moveToFirst()) {
            do {
                result.add(fromCursor(t, cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()

        return result
    }

    fun getSearchColumns( searchColumns: MutableList<String> ) : Array<String>? {
        return Array( searchColumns.size, {
            "${searchColumns[it]} $LIKE"
        } )
    }

    protected fun getJSONFromFile(filename: String): String {
        try {
            val s = "$DATABASE_DIRECTORY/$databaseName/$filename"
            val stream = context.assets.open(s)

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

    fun <T> fromCursor(t: Class<T>, cursor: Cursor): T {
        val obj = getJsonObject(cursor)

        if (t == Item::class.java) {
            val who = obj.getString("who")
            obj.remove("who")
            obj.put("who", JSONArray(who))
        }

        return gson.fromJson(obj.toString(), t)
    }


    companion object {
        fun exists(context: Context, database: String): Boolean {
            val dbFile = context.getDatabasePath(database)
            return dbFile.exists()
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
