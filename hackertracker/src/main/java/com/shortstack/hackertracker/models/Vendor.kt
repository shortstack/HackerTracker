package com.shortstack.hackertracker.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vendor(
        val id: Int,
        val name: String,
        val description: String?,
        val link: String?,
        val partner: Boolean,
        val updatedAt: String,
        val conference: String
) : Parcelable

