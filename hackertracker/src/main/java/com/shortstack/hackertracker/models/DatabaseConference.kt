package com.shortstack.hackertracker.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

/**
 * Created by Chris on 6/7/2018.
 */
data class DatabaseConference(
        @Embedded
        val conference: Conference
) {
    @Relation(parentColumn = "directory", entityColumn = "con")
    var types: List<Type> = emptyList()
}