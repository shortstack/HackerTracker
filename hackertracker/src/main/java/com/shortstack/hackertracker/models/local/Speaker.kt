package com.shortstack.hackertracker.models.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.math.absoluteValue

@Parcelize
data class Speaker(
        val name: String,
        val description: String,
        val link: String,
        val twitter: String,
        val title: String
) : Parcelable {

    val id: Int
        get() = hashCode().absoluteValue
}