package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row_speaker.view.*


class SpeakerView : LinearLayout {

    constructor(context: Context, speaker: FirebaseSpeaker) : super(context) {
        speaker_name.text = speaker.name

        speaker_description.text = if (speaker.title.isEmpty()) {
            context.getString(R.string.speaker_default_title)
        } else {
            speaker.title
        }

        val colours = context.resources.getStringArray(R.array.colors)
        val color = Color.parseColor(colours[speaker.id % colours.size])
        card.setCardBackgroundColor(color)

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
