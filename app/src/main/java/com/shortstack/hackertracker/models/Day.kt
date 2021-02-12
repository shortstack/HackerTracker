package com.shortstack.hackertracker.models

import java.text.SimpleDateFormat
import java.util.*

class Day(date: Date) : Date() {
    init {
        this.time = date.time
    }

    override fun toString() = SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(this)
}