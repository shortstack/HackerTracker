package com.advice.schedule.models.firebase

import com.google.firebase.Timestamp

data class FirebaseArticle(
    val id: Int = -1,
    val name: String = "",
    val text: String = "",
    val hidden: Boolean = false,
    val conference: String? = null,
    val updated_at: Timestamp? = null,
)