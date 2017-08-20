package com.shortstack.hackertracker.Model

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