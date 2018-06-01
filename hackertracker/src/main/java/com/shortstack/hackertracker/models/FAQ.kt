package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class FAQ(
        @PrimaryKey(autoGenerate = true)
        val index: Int,
        val question: String,
        val answer: String)