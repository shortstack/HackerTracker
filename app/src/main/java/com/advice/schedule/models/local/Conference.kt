package com.advice.schedule.models.local

import android.os.Parcelable
import com.advice.schedule.models.firebase.FirebaseMap
import com.advice.schedule.utilities.MyClock
import com.advice.schedule.utilities.now
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Conference(
        val id: Int,
        val name: String,
        val description: String,
        val conduct: String?,
        val support: String?,
        val code: String,
        val maps: ArrayList<FirebaseMap>,
        val startDate: Date,
        val endDate: Date,
        val timezone: String,
        var isSelected: Boolean = false
) : Parcelable {

    val hasFinished: Boolean
        get() = MyClock().now().after(endDate)
}