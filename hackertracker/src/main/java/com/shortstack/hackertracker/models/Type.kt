package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Type(
        @PrimaryKey(autoGenerate = true)
        val index: Int,
        @SerializedName("event_type")
        val type: String,
        val isSelected: Int = 0)