package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseLocation(
        val name: String = "",
        val hotel: String? = null,
        val conference: String = ""
) : Parcelable