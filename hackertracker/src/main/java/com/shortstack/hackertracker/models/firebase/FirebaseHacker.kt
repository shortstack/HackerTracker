package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseHacker(
        val id: String = "",
        val username: String? = null,
        val token: String = ""
) : Parcelable