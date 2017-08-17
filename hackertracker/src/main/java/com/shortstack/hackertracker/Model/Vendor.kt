package com.shortstack.hackertracker.Model

import android.content.ContentValues
import android.database.Cursor
import android.text.TextUtils
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class Vendors {

    lateinit var vendors: Array<Vendor>

    class Vendor : Serializable {

        var title: String? = null
        var description: String? = null
        var link: String? = null
        var partner: Int = 0

        val isPartner: Boolean
            get() = partner == 1

        fun hasLink(): Boolean {
            return !TextUtils.isEmpty(link)
        }

        fun getContentValues(gson: Gson): ContentValues {
            val values = ContentValues()

            val json = gson.toJson(this)
            try {
                val `object` = JSONObject(json)

                val keys = `object`.keys()
                var key: String
                while (keys.hasNext()) {
                    key = keys.next()
                    values.put(key, `object`.getString(key))
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return values
        }

        companion object {
            fun CursorToCompany(gson: Gson, cursor: Cursor): Vendor {
                val obj = JSONObject()

                val totalColumn = cursor.columnCount

                for (i in 0..totalColumn - 1) {
                    try {
                        obj.put(cursor.getColumnName(i), cursor.getString(i))
                    } catch (e: Exception) {
                        Logger.e(e, "Failed to convert Cursor into JSONObject.")
                    }
                }

                return gson.fromJson(obj.toString(), Vendor::class.java)
            }
        }
    }
}
