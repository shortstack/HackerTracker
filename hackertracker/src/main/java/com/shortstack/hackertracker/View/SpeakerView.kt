package com.shortstack.hackertracker.View

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.shortstack.hackertracker.Model.Speaker
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.row_speaker.view.*

class SpeakerView(context: Context, speaker: Speaker) : LinearLayout(context) {

    init {
        inflate()
        render(speaker)
    }

    private fun render(speaker: Speaker) {
        speaker_name.text = speaker.name
        speaker_description.text = speaker.bio
    }


    private fun inflate() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.row_speaker, null)

        addView(view)
    }
}
