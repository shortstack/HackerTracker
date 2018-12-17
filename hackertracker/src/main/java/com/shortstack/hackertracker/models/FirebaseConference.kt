package com.shortstack.hackertracker.models

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Chris on 2018-12-15.
 */
data class FirebaseConference(
        val id: Int = 0,
        val name: String = "",
        val description: String = "",
        val code: String = "",
        val events: HashMap<String, FirebaseEvent> = HashMap(),
        val types: HashMap<String, FirebaseType> = HashMap()
)

data class FirebaseEvent(
        val id: Int = -1,
        val conference: String = "",
        val title: String = "",
        val description: String = "",
        val begin: String = "",
        val end: String = "",

        val type: Map<String, Boolean> = HashMap(),
        val location: Map<String, Boolean> = HashMap()
) {

    val start: Date
        get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(begin)

    val date: Date
        get() {
            return Calendar.getInstance().apply {
                time = start

                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }
}

data class FirebaseType(
        val id: Int = -1,
        val name: String = "",
        val conference: String = "",
        val color: String = ""
)

data class FirebaseLocation(
        val name: String = "",
        val conference: String = ""
)