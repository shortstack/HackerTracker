package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseType(
        val id: Int = -1,
        val name: String = "",
        val conference: String = "",
        val color: String = "#343434"
) : Parcelable {

    val filtered: Boolean
        get() = name.contains("Workshop", true) || name.contains("Contest", true)

}