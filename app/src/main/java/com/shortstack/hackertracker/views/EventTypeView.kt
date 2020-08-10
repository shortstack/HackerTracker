package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.view_event_type.view.*

class EventTypeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_event_type, this)
        orientation = VERTICAL
    }

    fun render(type: Type) {
        event_type_text.text = type.shortName

        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.category_tint, value, true)
        val id = value.resourceId

        val color = if (id > 0) {
            ContextCompat.getColor(context, id)
        } else {
            Logger.d("color: ${type.color}")
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
        event_type_dot.background = drawable
    }
}
