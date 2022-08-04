package com.advice.schedule.models.local

import android.os.Parcelable
import com.advice.schedule.utilities.MyClock
import com.advice.schedule.utilities.now
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class LocationContainer(
    val title: String,
    val defaultStatus: String?,
    val depth: Int,
    val schedule: List<LocationSchedule>,
    val isChildrenExpanded: Boolean = true,
    val isExpanded: Boolean = true
) : Parcelable {

    var status: LocationStatus = LocationStatus.Closed

    fun getCurrentStatus(): LocationStatus {
        if (schedule.isEmpty()) {
            return when (defaultStatus) {
                "open" -> LocationStatus.Open
                "closed" -> LocationStatus.Closed
                else -> LocationStatus.Unknown
            }
        }

        val now = MyClock().now()
        val isActive = schedule.any {
            val begin = parse(it.begin)
            val end = parse(it.end)
            begin != null && end != null && begin.before(now) && end.after(now)
        }
        if (isActive) {
            return LocationStatus.Open
        }

        return LocationStatus.Closed
    }

    private fun parse(date: String): Date? {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)
        } catch (ex: Exception) {
            null
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is LocationContainer) {
            return title == other.title
        }
        return super.equals(other)
    }
}

fun LocationContainer.isExpanded(isExpanded: Boolean): LocationContainer {
    val status = status
    return copy(isExpanded = isExpanded).apply {
        setStatus(status)
    }
}

fun LocationContainer.isChildrenExpanded(isExpanded: Boolean): LocationContainer {
    val status = status
    return copy(isChildrenExpanded = isExpanded).apply {
        setStatus(status)
    }
}

fun LocationContainer.setStatus(status: LocationStatus) {
    this.status = status
}