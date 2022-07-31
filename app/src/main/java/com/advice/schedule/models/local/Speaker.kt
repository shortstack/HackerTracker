package com.advice.schedule.models.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.math.absoluteValue

@Parcelize
data class Speaker(
        val id: Int,
        val name: String,
        val description: String,
        val link: String,
        val twitter: String,
        val title: String
) : Parcelable