package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.math.absoluteValue

@Parcelize
data class FirebaseSpeaker(
        val name: String = "",
        val description: String = "",
        val link: String = "",
        val twitter: String = "",
        val title: String = "",

        val events: ArrayList<FirebaseEvent> = ArrayList()
) : Parcelable {

    val id: Int
        get() = hashCode().absoluteValue
}