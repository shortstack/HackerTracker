package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseConference(
        val id: Int = 0,
        val name: String = "",
        val description: String = "",
        val code: String = "",
        @field:JvmField
        @PropertyName("is_selected")
        var isSelected: Boolean = false,
        val maps: ArrayList<FirebaseMap> = ArrayList()
) : Parcelable