package com.shortstack.hackertracker.ui.search

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row_speaker.view.*
import kotlin.math.absoluteValue

class SpeakerRenderer : Renderer<FirebaseSpeaker>() {

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.row_speaker, container, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView?.setOnClickListener {
            (context as? MainActivity)?.navigate(content)
        }
    }

    override fun render(p0: MutableList<Any>?) {
        rootView.speaker_name.text = content.name
        rootView.speaker_description.text = if (content.title.isBlank()) {
            context.getString(R.string.speaker_default_title)
        } else {
            content.title
        }

        val colours = context.resources.getStringArray(R.array.colors)
        val color = Color.parseColor(colours[content.hashCode().absoluteValue % colours.size])
        rootView.card.setCardBackgroundColor(color)
    }
}
