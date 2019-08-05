package com.shortstack.hackertracker.models.local

data class FAQ(
        val id: Int,
        val question: String,
        val answer: String,
        var isExpanded: Boolean = false
)