package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.RowSpeakerBinding
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.ui.activities.MainActivity


class SpeakerView(context: Context, speaker: Speaker) : LinearLayout(context) {

    private val binding = RowSpeakerBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.speakerName.text = speaker.name

        binding.speakerDescription.text = if (speaker.title.isEmpty()) {
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

        binding.category.setBackgroundColor(color)

        setOnClickListener {
            (context as? MainActivity)?.navigate(speaker)
        }
    }
}
