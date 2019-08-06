package com.shortstack.hackertracker.ui.information.speakers

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

            val colours = context.resources.getStringArray(R.array.colors)
            val color = Color.parseColor(colours[speaker.id % colours.size])
            category.setBackgroundColor(color)

            setOnClickListener {
                (context as? MainActivity)?.navigate(speaker)
            }
        }
    }

}