package com.shortstack.hackertracker.models.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
        val name: String,
        val conference: String
) : Parcelable