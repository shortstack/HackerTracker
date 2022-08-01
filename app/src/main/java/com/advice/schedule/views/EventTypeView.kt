package com.advice.schedule.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.local.Type
import com.advice.schedule.ui.activities.MainActivity
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.ViewEventTypeBinding

class EventTypeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val binding = ViewEventTypeBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL

        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.EventView, 0, 0)
        try {
            val showDot = array.getBoolean(R.styleable.EventView_eventView_showDot, true)
            binding.eventTypeDot.isVisible = showDot
            val hasSpacer = array.getBoolean(R.styleable.EventView_eventView_spacer, false)
            binding.spacer.isVisible = hasSpacer
        } finally {
            array.recycle()
        }
    }

    fun render(type: FirebaseTag) {
        binding.eventTypeText.text = type.label

        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.category_tint, value, true)
        val id = value.resourceId

        val color = if (id > 0) {
            ContextCompat.getColor(context, id)
        } else {
            if (type.color == "#FFFFFF") {
                val theme = (context as MainActivity).theme
                val outValue = TypedValue()
                theme.resolveAttribute(
                    com.google.android.material.R.attr.colorOnSurface,
                    outValue,
                    true
                )
                outValue.data
            } else {
                Color.parseColor(type.color)
            }
        }

        val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()

        drawable?.setTint(color)
        binding.eventTypeDot.background = drawable
    }
}
