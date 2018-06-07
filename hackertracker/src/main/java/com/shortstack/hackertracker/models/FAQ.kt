package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class FAQ(
        @PrimaryKey(autoGenerate = true)
        val index: Int,
        var con: String,
        val question: String,
        val answer: String)