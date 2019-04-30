package com.shortstack.hackertracker.ui.speakers

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row_speaker.view.*

class SpeakerViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun render(speaker: FirebaseSpeaker) {
        view.apply {
            speaker_name.text = speaker.name
            speaker_description.text = if (speaker.title.isBlank()) {
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
    }

}