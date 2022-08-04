package com.advice.schedule.models.local

data class FAQQuestion(
    val id: Int,
    val question: String,
    val isExpanded: Boolean = false
)

data class FAQAnswer(
    val id: Int,
    val answer: String,
    val isExpanded: Boolean = false
)