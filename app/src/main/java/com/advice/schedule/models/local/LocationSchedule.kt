package com.advice.schedule.models.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationSchedule(
    val begin: String = "",
    val end: String = "",
    val notes: String? = null,
    val status: String = "closed"
) : Parcelable