package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseEvent(
    val id: Long = -1,
    val conference: String = "",
    val title: String = "",
    val android_description: String = "",
    val description: String = "",
    val begin: String = "",
    val end: String = "",
    val link: String = "",
    val updated: String = "",
    val speakers: ArrayList<FirebaseSpeaker> = ArrayList(),
    val type: FirebaseType = FirebaseType(),
    val location: FirebaseLocation = FirebaseLocation(),
    val links: List<FirebaseAction> = ArrayList(),

    val youtube_url: String? = null,
    val begin_timestamp: Timestamp? = null,
    val end_timestamp: Timestamp? = null,
    val updated_timestamp: Timestamp? = null,
    val internal_notes: String? = null,
    val includes: String? = null,
    val download_url: String? = null,
    val tags: String? = null,

    val hidden: Boolean = false
) : Parcelable