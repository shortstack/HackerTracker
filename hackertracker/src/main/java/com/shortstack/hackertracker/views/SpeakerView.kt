package com.shortstack.hackertracker.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.shortstack.hackertracker.models.Speaker
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row_speaker.view.*

class SpeakerView(context: Context, speaker: Speaker) : LinearLayout(context) {

    init {
        inflate()
        render(speaker)

        setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("EXTRA_SPEAKER", speaker)

            (context as MainActivity).navController.navigate(R.id.nav_speaker, bundle)
        }
    }

    private fun render(speaker: Speaker) {
        speaker_initials.text = speaker.name.trim().split(" ").joinToString("") { it.first().toString() }
        speaker_name.text = speaker.name
        speaker_description.text = speaker.title ?: context.getString(R.string.speaker_default_title)
    }


    private fun inflate() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.row_speaker, null)

        addView(view)
    }
}
