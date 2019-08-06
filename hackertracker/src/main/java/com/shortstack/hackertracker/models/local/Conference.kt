package com.shortstack.hackertracker.models.local

import android.os.Parcelable
import com.shortstack.hackertracker.models.firebase.FirebaseMap
import com.shortstack.hackertracker.utilities.MyClock
import com.shortstack.hackertracker.utilities.now
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Conference(
        val id: Int,
        val name: String,
        val description: String,
        val conduct: String?,
        val code: String,
        val maps: ArrayList<FirebaseMap>,
        val startDate: Date,
        val endDate: Date,
        var isSelected: Boolean = false
) : Parcelable {

    val hasFinished: Boolean
        get() = MyClock().now().after(endDate)
}