package com.shortstack.hackertracker.ui.information.speakers

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.models.local.Speaker

// todo: replace with ListAdapter
class SpeakerAdapter : RecyclerView.Adapter<SpeakerViewHolder>() {

    private val speakers = ArrayList<Speaker>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerViewHolder {
        return SpeakerViewHolder.inflate(parent)
    }

    override fun getItemCount() = speakers.size

    override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
        holder.render(speakers[position])
    }

    fun setSpeakers(list: List<Speaker>) {

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
