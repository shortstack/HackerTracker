package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(foreignKeys = [(ForeignKey(entity = (Conference::class), parentColumns = [("directory")], childColumns = [("con")], onDelete = ForeignKey.CASCADE))])
data class Type(
        @PrimaryKey(autoGenerate = true)
        val index: Int,
        @SerializedName("event_type")
        val type: String,
        val colour: String,
        var isSelected: Boolean,
        var con: String)