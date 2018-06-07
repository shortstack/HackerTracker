package com.shortstack.hackertracker.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

/**
 * Created by Chris on 4/4/2018.
 */
data class DatabaseEvent(
        @Embedded
        val event: Event
) {
    @Relation(parentColumn = "type", entityColumn = "type")
    var type: List<Type> = emptyList()
}

