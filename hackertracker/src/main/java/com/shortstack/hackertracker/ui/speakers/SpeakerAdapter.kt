package com.shortstack.hackertracker.ui.speakers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseSpeaker

class SpeakerAdapter : RecyclerView.Adapter<SpeakerViewHolder>() {

    private val speakers = ArrayList<FirebaseSpeaker>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_speaker, parent, false)
        return SpeakerViewHolder(view)
    }

    override fun getItemCount() = speakers.size

    override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
        val speaker = speakers[position]

        holder.render(speaker)
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

}
