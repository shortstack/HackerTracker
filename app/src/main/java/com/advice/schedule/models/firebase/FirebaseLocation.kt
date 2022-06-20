package com.advice.schedule.models.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseLocation(
    val id: Long? = null,
    val name: String = "",
    val hotel: String? = null,
    val conference: String = "",

    val internal_notes: String? = null,
    val twitch_url: String? = null,
    val youtube_url: String? = null,
    val updated_at: String? = null,
    val eventdescriptionfooter: String? = null,
) : Parcelable