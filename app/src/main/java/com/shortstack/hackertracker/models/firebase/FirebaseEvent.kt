package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseEvent(
    val id: Int = -1,
    val conference: String = "",
    val title: String = "",
    val description: String = "",
    val begin: String = "",
    val end: String = "",
    val link: String = "",
    val updated: String = "",
    val speakers: ArrayList<FirebaseSpeaker> = ArrayList(),
    val type: FirebaseType = FirebaseType(),
    val location: FirebaseLocation = FirebaseLocation(),
    val links: List<FirebaseAction> = ArrayList(),
    val hidden: Boolean = false
) : Parcelable