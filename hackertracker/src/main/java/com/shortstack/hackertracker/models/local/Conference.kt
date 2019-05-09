package com.shortstack.hackertracker.models.local

import android.os.Parcelable
import com.shortstack.hackertracker.models.firebase.FirebaseMap
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Conference(
        val id: Int,
        val name: String,
        val description: String,
        val code: String,
        val maps: ArrayList<FirebaseMap>,
        var isSelected: Boolean = false
) : Parcelable