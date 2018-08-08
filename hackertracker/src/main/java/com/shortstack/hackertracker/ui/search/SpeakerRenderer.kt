package com.shortstack.hackertracker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Speaker
import kotlinx.android.synthetic.main.row_speaker.view.*

class SpeakerRenderer : Renderer<Speaker>() {

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.row_speaker, container, false)
    }

    override fun render(p0: MutableList<Any>?) {
        rootView.speaker_name.text = content.name
        rootView.speaker_description.text = if (content.description.isBlank()) {
            context.getString(R.string.speaker_default_title)
        } else {
            content.description
        }
    }
}
