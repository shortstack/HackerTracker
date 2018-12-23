package com.shortstack.hackertracker.ui.search

import androidx.recyclerview.widget.DiffUtil
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.FirebaseLocation
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.ui.schedule.renderers.EventRenderer
import com.shortstack.hackertracker.views.EventView

/**
 * Created by Chris on 7/29/2018.
 */
class SearchAdapter : RendererAdapter<Any>(RendererBuilder.create<FirebaseEvent>()
        .bind(FirebaseEvent::class.java, EventRenderer(EventView.DISPLAY_MODE_FULL))
        .bind(FirebaseLocation::class.java, LocationRenderer())
        .bind(FirebaseSpeaker::class.java, SpeakerRenderer())
        .bind(String::class.java, HeaderRenderer()).rendererBuilder) {

    var state: State = State.INIT
    var query: String? = null

    fun setList(elements: List<Any>) {
        state = when {
            query.isNullOrBlank() -> State.INIT
            elements.isNotEmpty() -> State.RESULTS
            else -> State.EMPTY
        }

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

                val left = collection[oldItemPosition]
                val right = elements[newItemPosition]
                if (left is FirebaseEvent && right is FirebaseEvent) {
                    return left.id == right.id
                }
                if (left is FirebaseLocation && right is FirebaseLocation) {
                    return left.name == right.name
                }
                if (left is FirebaseSpeaker && right is FirebaseSpeaker) {
                    return left.name == right.name
                }
                if (left is String && right is String) {
                    return left == right
                }
                return false
            }

            override fun getOldListSize() = collection.size

            override fun getNewListSize() = elements.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return true
            }
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