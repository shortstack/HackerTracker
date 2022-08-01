package com.advice.schedule.models.firebase

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseEvent(
    @PropertyName("id")
    val id: Long = -1,
    @PropertyName("conference")
    val conference: String = "",
    @PropertyName("title")
    val title: String = "",
    @PropertyName("android_description")
    val android_description: String = "",

    @PropertyName("speakers")
    val speakers: ArrayList<FirebaseSpeaker> = ArrayList(),
    @PropertyName("type")
    val type: FirebaseType = FirebaseType(),
    @PropertyName("location")
    val location: FirebaseLocation = FirebaseLocation(),
    @PropertyName("links")
    val links: List<FirebaseAction> = emptyList(),

    val tag_ids: List<Long> = emptyList(),

    @PropertyName("begin_timestamp")
    val begin_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("end_timestamp")
    val end_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("updated_timestamp")
    val updated_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("hidden")
    val hidden: Boolean = false
) : Parcelable