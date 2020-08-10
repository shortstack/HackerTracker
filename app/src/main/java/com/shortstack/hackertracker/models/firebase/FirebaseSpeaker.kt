package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.math.absoluteValue

@Parcelize
data class FirebaseSpeaker(
        val id: Int = -1,
        val name: String = "",
        val description: String = "",
        val link: String = "",
        val twitter: String = "",
        val title: String = "",
        val hidden: Boolean = false
) : Parcelable