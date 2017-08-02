package com.shortstack.hackertracker.View

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import butterknife.ButterKnife
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
    }


    private fun inflate() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.row_speaker, null)
        ButterKnife.bind(this, view)

        addView(view)
    }
}
