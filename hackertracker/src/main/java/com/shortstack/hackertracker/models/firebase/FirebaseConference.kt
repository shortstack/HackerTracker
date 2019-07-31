package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class FirebaseConference(
        val id: Int = 0,
        val name: String = "",
        val description: String = "",
        val code: String = "",
        val maps: ArrayList<FirebaseMap> = ArrayList(),
        val start_date: String = "",
        val end_date: String = "",
        val start_timestamp: Timestamp = Timestamp(Date()),
        val end_timestamp: Timestamp = Timestamp(Date()),
        val hidden: Boolean = false
) : Parcelable