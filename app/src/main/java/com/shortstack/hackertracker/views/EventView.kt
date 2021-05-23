package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.database.ReminderManager
import com.shortstack.hackertracker.databinding.RowEventBinding
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utilities.Analytics
import org.koin.core.KoinComponent
import org.koin.core.inject

class EventView : FrameLayout, KoinComponent {

    private val binding = RowEventBinding.inflate(LayoutInflater.from(context), this, true)

    // todo: extract
    private val analytics: Analytics by inject()
    private val database: DatabaseManager by inject()
    private val reminder: ReminderManager by inject()

    var displayMode: Int = DISPLAY_MODE_MIN

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        display: Int = DISPLAY_MODE_MIN
    ) : super(context, attrs) {
        displayMode = display
        init()
    }

    constructor(context: Context, event: Event, display: Int = DISPLAY_MODE_FULL) : super(context) {
        displayMode = display
        init()
        setContent(event)
    }

    private fun init() {
        setDisplayMode()
    }

    fun setContent(event: Event) {
        render(event)
    }

    private fun setDisplayMode() {
        when (displayMode) {
            DISPLAY_MODE_MIN -> {
                val width = context.resources.getDimension(R.dimen.event_view_min_guideline).toInt()
                binding.guideline.setGuidelineBegin(width)
                binding.typesContainer.visibility = View.GONE
            }
            DISPLAY_MODE_FULL -> {
                val width = context.resources.getDimension(R.dimen.time_width).toInt()
                binding.guideline.setGuidelineBegin(width)
                binding.typesContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun render(event: Event) {
        binding.title.text = event.title

        // Stage 2
        if (displayMode == DISPLAY_MODE_FULL) {
            binding.location.text = event.location.name
        } else {
            binding.location.text = event.getFullTimeStamp(context) + " / " + event.location.name
        }


        renderCategoryColour(event)
        updateBookmark(event)

        setOnClickListener {
            (context as? MainActivity)?.navigate(event)
        }

        binding.starBar.setOnClickListener {
            onBookmarkClick(event)
        }
    }

    private fun renderCategoryColour(event: Event) {
        val type = event.types.first()

        binding.type1.render(type)
        if (event.types.size > 1) {
            binding.type2.render(event.types.last())
            binding.type2.visibility = View.VISIBLE
        } else {
            binding.type2.visibility = View.GONE
        }

        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.category_tint, value, true)
        val id = value.resourceId

        val color = if (id > 0) {
            ContextCompat.getColor(context, id)
        } else {
            Color.parseColor(type.color)
        }
        binding.category.setBackgroundColor(color)
    }


    private fun updateBookmark(event: Event) {
        val isBookmarked = event.isBookmarked

        val drawable = if (isBookmarked) {
            R.drawable.ic_star_accent_24dp
        } else {
            R.drawable.ic_star_border_white_24dp
        }

        binding.starBar.setImageResource(drawable)
    }

    private fun onBookmarkClick(event: Event) {
        event.isBookmarked = !event.isBookmarked
        database.updateBookmark(event)

        if (event.isBookmarked) {
            reminder.setReminder(event)
        } else {
            reminder.cancel(event)
        }

        val action =
            if (event.isBookmarked) Analytics.EVENT_BOOKMARK else Analytics.EVENT_UNBOOKMARK
        analytics.onEventAction(action, event)

        updateBookmark(event)
    }

    companion object {
        const val DISPLAY_MODE_MIN = 0
        const val DISPLAY_MODE_FULL = 1
    }
}
