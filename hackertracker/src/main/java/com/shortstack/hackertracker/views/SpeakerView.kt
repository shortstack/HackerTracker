package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.shortstack.hackertracker.models.Speaker
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.speakers.SpeakerFragment
import kotlinx.android.synthetic.main.row_speaker.view.*

class SpeakerView(context: Context, speaker: Speaker) : LinearLayout(context) {

    init {
        inflate()
        render(speaker)

        setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(SpeakerFragment.EXTRA_SPEAKER, speaker)

            (context as MainActivity).navController.navigate(R.id.nav_speaker, bundle)
        }
    }

    private fun inflate() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.row_speaker, null)

        addView(view)
    }

    private fun render(speaker: Speaker) {
        speaker_name.text = speaker.name
        speaker_description.text = speaker.title ?: context.getString(R.string.speaker_default_title)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val colours = context.resources.getStringArray(R.array.colors)
            val color = Color.parseColor(colours[speaker.id % colours.size])

            val drawable = ContextCompat.getDrawable(context, R.drawable.speaker_image)?.mutate()
            drawable?.setTint(color)
            speaker_image.background = drawable
        }
    }
}
