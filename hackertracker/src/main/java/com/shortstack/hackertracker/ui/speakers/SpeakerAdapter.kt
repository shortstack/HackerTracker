package com.shortstack.hackertracker.ui.speakers

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row_speaker.view.*

class SpeakerAdapter : RecyclerView.Adapter<SpeakerAdapter.ViewHolder>() {

    private val speakers = ArrayList<FirebaseSpeaker>()

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

    fun setSpeakers(list: List<FirebaseSpeaker>) {

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return speakers[oldItemPosition].id == list[newItemPosition].id
            }

            override fun getOldListSize() = speakers.size

            override fun getNewListSize() = list.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return speakers[oldItemPosition] == list[newItemPosition]
            }
        })

        speakers.clear()
        speakers.addAll(list)

        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}
