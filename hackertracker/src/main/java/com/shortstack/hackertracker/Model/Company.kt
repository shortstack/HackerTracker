package com.shortstack.hackertracker.Model

import android.database.Cursor
import android.text.TextUtils
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import org.json.JSONObject

import java.io.Serializable

class Company : Serializable {

    var id: Int = 0
    var title: String? = null
    var description: String? = null
    var link: String? = null
    var partner: Int = 0

    val isPartner: Boolean
        get() = partner == 1

    fun hasLink(): Boolean {
        return !TextUtils.isEmpty(link)
    }

    companion object {
        fun CursorToCompany(gson: Gson, cursor: Cursor): Company {
            val obj = JSONObject()

            val totalColumn = cursor.columnCount

            for (i in 0..totalColumn - 1) {
                try {
                    obj.put(cursor.getColumnName(i), cursor.getString(i))
                } catch (e: Exception) {
                    Logger.e(e, "Failed to convert Cursor into JSONObject.")
                }
            }

            return gson.fromJson(obj.toString(), Company::class.java)
        }
    }
}
