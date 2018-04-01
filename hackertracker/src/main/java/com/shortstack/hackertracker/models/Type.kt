package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Type(
        @PrimaryKey(autoGenerate = false)
        @SerializedName("event_type")
        val type: String,
        val colour: String,
        var isSelected: Boolean) {

    init {
        isSelected = true
    }

}