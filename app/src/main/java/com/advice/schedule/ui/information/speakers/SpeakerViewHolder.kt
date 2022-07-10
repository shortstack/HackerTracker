package com.advice.schedule.ui.information.speakers

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.ui.activities.MainActivity
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.RowSpeakerBinding

class SpeakerViewHolder(private val binding: RowSpeakerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(speaker: Speaker) = with(binding) {
        val context = root.context

        speakerName.text = speaker.name
        speakerDescription.text = if (speaker.title.isBlank()) {
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

        root.setOnClickListener {
            (context as? MainActivity)?.showSpeaker(speaker)
        }
    }

    companion object {

        fun inflate(parent: ViewGroup): SpeakerViewHolder {
            val binding = RowSpeakerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SpeakerViewHolder(binding)
        }
    }
}