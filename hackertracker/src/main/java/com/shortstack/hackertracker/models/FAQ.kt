package com.shortstack.hackertracker.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [(ForeignKey(entity = (Conference::class), parentColumns = [("code")], childColumns = [("conference")], onDelete = ForeignKey.CASCADE))])
data class FAQ(
        @PrimaryKey(autoGenerate = true)
        val index: Int,
        val conference: String,
        val question: String,
        val answer: String)