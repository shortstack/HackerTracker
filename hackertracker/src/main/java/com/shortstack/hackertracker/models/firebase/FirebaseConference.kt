package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseConference(
        val id: Int = 0,
        val name: String = "",
        val description: String = "",
        val code: String = "",
        val maps: ArrayList<FirebaseMap> = ArrayList()
) : Parcelable