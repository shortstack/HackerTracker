package com.shortstack.hackertracker.database

import androidx.room.TypeConverter
import java.util.Date


/**
 * Created by Chris on 3/31/2018.
 */
class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromInt(value: Int?): Boolean? {
        return if (value == null) null else value == 1
    }

    @TypeConverter
    fun booleanToInt(value: Boolean?): Int? {
        return if (value == true) 1 else 0
    }
}