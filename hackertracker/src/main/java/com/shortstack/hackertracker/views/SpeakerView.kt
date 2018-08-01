package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Speaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row_speaker.view.*

class SpeakerView(context: Context, speaker: Speaker) : LinearLayout(context) {

    init {
        inflate()
        render(speaker)

        setOnClickListener {
            (context as? MainActivity)?.navigate(speaker)
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
