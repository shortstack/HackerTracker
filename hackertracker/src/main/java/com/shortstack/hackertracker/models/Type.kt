package com.shortstack.hackertracker.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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