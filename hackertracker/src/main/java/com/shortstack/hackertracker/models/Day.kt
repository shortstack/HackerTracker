package com.shortstack.hackertracker.models

import java.util.*

class Day(date: Date) : Date() {
    init {
        this.time = date.time
    }
}

class Time(date: Date) : Date() {
    init {
        this.time = date.time
    }
}