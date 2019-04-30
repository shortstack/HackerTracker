package com.shortstack.hackertracker.ui.search

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row_speaker.view.*

class SpeakerViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun inflate(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.row_speaker, container, false)
    }

    fun render(speaker: FirebaseSpeaker) {
        view.speaker_name.text = speaker.name
        view.speaker_description.text = if (speaker.title.isBlank()) {
            view.context.getString(R.string.speaker_default_title)
        } else {
            speaker.title
        }

        val colours = view.context.resources.getStringArray(R.array.colors)
        val color = Color.parseColor(colours[speaker.id % colours.size])
        view.card.setCardBackgroundColor(color)

        view.setOnClickListener {
            (view.context as? MainActivity)?.navigate(speaker)
        }
    }
}
