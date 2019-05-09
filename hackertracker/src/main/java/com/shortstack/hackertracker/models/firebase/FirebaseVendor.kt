package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseVendor(
        val id: Int,
        val name: String,
        val description: String?,
        val link: String?,
        val partner: Boolean,
        val updatedAt: String,
        val conference: String
) : Parcelable

