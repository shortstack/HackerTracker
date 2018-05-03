package com.shortstack.hackertracker.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

/**
 * Created by Chris on 4/4/2018.
 */
class DatabaseEvent {
    @Embedded
    var event: Event? = null

    @Relation(parentColumn = "type", entityColumn = "type", entity = Type::class)
    var type: List<Type>? = null
}


