package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row_speaker.view.*


class SpeakerView : LinearLayout {

    constructor(context: Context, speaker: Speaker) : super(context) {
        speaker_name.text = speaker.name

        speaker_description.text = if (speaker.title.isEmpty()) {
            context.getString(R.string.speaker_default_title)
        } else {
            speaker.title
        }

        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.category_tint, value, true)
        val id = value.resourceId

        val color = if (id > 0) {
            ContextCompat.getColor(context, id)
        } else {
            val colours = context.resources.getStringArray(R.array.colors)
            Color.parseColor(colours[speaker.id % colours.size])
        }

        category.setBackgroundColor(color)

        setOnClickListener {
            (context as? MainActivity)?.navigate(speaker)
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        LayoutInflater.from(context).inflate(R.layout.row_speaker, this)
    }

}
