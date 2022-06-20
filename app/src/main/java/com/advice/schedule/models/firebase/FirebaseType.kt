package com.advice.schedule.models.firebase

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseType(
    val id: Int = -1,
    val name: String = "",
    val description: String = "",
    val conference: String = "",
    val color: String = "#343434",
    val discord_url: String? = null,
    val subforum_url: String? = null,
    val youtube_url: String? = null,
    val eventdescriptionfooter: String? = null,
    val updated_at: String? = null,
    val tags: String? = null
) : Parcelable