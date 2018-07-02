package com.shortstack.hackertracker.models

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Created by Chris on 6/7/2018.
 */
data class DatabaseConference(
        @Embedded
        val conference: Conference
) {
    @Relation(parentColumn = "code", entityColumn = "conference")
    var types: List<Type> = emptyList()
}