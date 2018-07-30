package com.shortstack.hackertracker.ui.search

import androidx.recyclerview.widget.DiffUtil
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.ui.schedule.renderers.EventRenderer

/**
 * Created by Chris on 7/29/2018.
 */
class SearchAdapter : RendererAdapter<DatabaseEvent>(RendererBuilder.create<DatabaseEvent>()
        .bind(DatabaseEvent::class.java, EventRenderer()).rendererBuilder) {

    var state: State = State.INIT
    var query: String? = null

    fun setList(elements: List<DatabaseEvent>) {
        state = when {
            query.isNullOrBlank() == true -> State.INIT
            elements.isNotEmpty() -> State.RESULTS
            else -> State.EMPTY
        }

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return collection[oldItemPosition].event.id == elements[newItemPosition].event.id
            }

            override fun getOldListSize() = collection.size

            override fun getNewListSize() = elements.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true

        })

        collection.clear()
        collection.addAll(elements)

        result.dispatchUpdatesTo(this)
    }

    enum class State {
        INIT,
        RESULTS,
        EMPTY
    }
}