package com.shortstack.hackertracker.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [(ForeignKey(entity = (Conference::class), parentColumns = [("directory")], childColumns = [("con")], onDelete = ForeignKey.CASCADE))])
data class FAQ(
        @PrimaryKey(autoGenerate = true)
        val index: Int,
        var con: String,
        val question: String,
        val answer: String)