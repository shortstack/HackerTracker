package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.ViewEventTypeBinding
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity

class EventTypeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val binding = ViewEventTypeBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL
    }

    fun render(type: Type) {
        binding.eventTypeText.text = type.shortName

        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.category_tint, value, true)
        val id = value.resourceId

        val color = if (id > 0) {
            ContextCompat.getColor(context, id)
        } else {
            if (type.color == "#FFFFFF") {
                val theme = (context as MainActivity).theme
                val outValue = TypedValue()
                theme.resolveAttribute(R.attr.colorOnPrimary, outValue, true)
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
