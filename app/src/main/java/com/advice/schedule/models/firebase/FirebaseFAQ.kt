package com.advice.schedule.models.firebase

data class FirebaseFAQ(
    val id: Int = -1,
    val conference: String = "",
    val question: String = "",
    val answer: String = "",
    val updated_at: String? = null
)