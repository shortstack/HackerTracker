package com.advice.schedule.models.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
        val name: String,
        val hotel: String?,
        val conference: String
) : Parcelable