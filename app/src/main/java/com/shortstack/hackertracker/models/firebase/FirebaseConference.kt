package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class FirebaseConference(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val codeofconduct: String? = null,
    val code: String = "",
    val maps: ArrayList<FirebaseMap> = ArrayList(),
    val start_date: String = "",
    val end_date: String = "",
    val begin_timestamp: Timestamp? = null,
    val start_timestamp: Timestamp = Timestamp(Date()),
    val end_timestamp: Timestamp = Timestamp(Date()),
    val timezone: String = "",
    val link: String? = null,
    //val updated_at: String? = null,
    val hidden: Boolean = false
) : Parcelable