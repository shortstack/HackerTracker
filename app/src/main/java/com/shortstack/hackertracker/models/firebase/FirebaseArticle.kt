package com.shortstack.hackertracker.models.firebase

data class FirebaseArticle(
        val id: Int = -1,
        val name: String = "",
        val text: String = "",
        val hidden: Boolean = false
)