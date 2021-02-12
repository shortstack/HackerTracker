package com.shortstack.hackertracker.ui.information.speakers

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row_speaker.view.*

class SpeakerViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): SpeakerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_speaker, parent, false)
            return SpeakerViewHolder(view)
        }
    }

    fun render(speaker: Speaker) {
        view.apply {
            speaker_name.text = speaker.name
            speaker_description.text = if (speaker.title.isBlank()) {
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
    }

}