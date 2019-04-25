package com.shortstack.hackertracker.ui.speakers

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row_speaker.view.*

class SpeakerAdapter(private val speakers: List<FirebaseSpeaker>) : RecyclerView.Adapter<SpeakerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_speaker, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = speakers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val speaker = speakers[position]

        holder.view.apply {
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

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}
