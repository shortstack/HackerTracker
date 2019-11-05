package com.shortstack.hackertracker.models.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Type(
        val id: Int,
        val name: String,
        val conference: String,
        val color: String,
        var isSelected: Boolean = false
) : Parcelable {
    val isBookmark: Boolean
        get() = name.contains("bookmark", true)

}