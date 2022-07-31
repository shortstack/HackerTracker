package com.advice.schedule.views

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.advice.schedule.utilities.MyClock
import com.advice.schedule.utilities.now
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.LocationViewBinding
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

class LocationView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private val binding = LocationViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setLocation(location: LocationContainer, onClickListener: ((LocationContainer) -> Unit)? = null) {
        binding.title.text = location.title

        binding.spacer.layoutParams.width = location.depth * 16.toPx
        binding.spacer.requestLayout()

        val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()

        val color = when (location.status) {
            LocationStatus.Open -> Color.GREEN
            LocationStatus.Closed -> Color.RED
            LocationStatus.Mixed -> Color.YELLOW
        }

        drawable?.setTint(color)
        binding.status.background = drawable

        if (onClickListener != null) {
            binding.root.setOnClickListener {
                onClickListener.invoke(location)
            }
        }
    }
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

@Parcelize
data class LocationContainer(
    val title: String,
    val depth: Int,
    val schedule: List<LocationSchedule>,
    val isChildrenExpanded: Boolean = true,
    val isExpanded: Boolean = true
) : Parcelable {

    var status: LocationStatus = LocationStatus.Closed

    fun getCurrentStatus(): LocationStatus {
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

@Parcelize
data class LocationSchedule(
    val begin: String = "",
    val end: String = "",
    val notes: String? = null,
    val status: String = "closed"
) : Parcelable

sealed class LocationStatus {
    object Open : LocationStatus()
    object Closed : LocationStatus()
    object Mixed : LocationStatus()
}