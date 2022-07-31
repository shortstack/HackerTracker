package com.advice.schedule.models.firebase

import android.os.Parcelable
import com.advice.schedule.views.LocationSchedule
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseLocation(
    val id: Long = -1,
    val name: String = "",
    val hotel: String? = null,
    val conference: String = "",
    // Schedule
    val default_status: String? = null,
    val hier_depth: Int = -1 ,
    val hier_extent_left: Int = -1 ,
    val hier_extent_right: Int = -1,
    val parent_id: Int = -1,
    val peer_sort_order: Int = -1,
    val schedule: List<LocationSchedule>? = null,
    val updated_at: String? = null,
) : Parcelable