package com.shortstack.hackertracker.ui.information.speakers

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.views.EventView
import kotlinx.android.synthetic.main.empty_text.*
import kotlinx.android.synthetic.main.fragment_speakers.*
import org.koin.android.ext.android.inject

class SpeakerFragment : Fragment() {

    companion object {
        private const val EXTRA_SPEAKER = "EXTRA_SPEAKER"

        fun newInstance(speaker: Speaker): SpeakerFragment {
            val fragment = SpeakerFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_SPEAKER, speaker)
            fragment.arguments = bundle

            return fragment
        }
    }

    private val database: DatabaseManager by inject()
    private val analytics: Analytics by inject()

    private val viewModel by lazy { ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_speakers, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = context ?: return


        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_white_24dp)
        toolbar.navigationIcon = drawable

        toolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.popBackStack()
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            val height = StatusBarSpacer.getStatusBarHeight(context, app_bar)
//            app_bar.setPadding(0, height, 0, 0)
//        }




        val speaker = arguments?.getParcelable(EXTRA_SPEAKER) as? Speaker

        viewModel.speakers.observe(this, Observer {
            val target = it.data?.find { it.id == speaker?.id }
            if(target != null) {
                showSpeaker(target)
            }
        })
    }

    private fun showSpeaker(speaker: Speaker) {
        analytics.log("Viewing speaker ${speaker.name}")

        collapsing_toolbar.title = speaker.name
        collapsing_toolbar.subtitle = if (speaker.title.isEmpty()) {
            context?.getString(R.string.speaker_default_title)
        } else {
            speaker.title
        }

        val url = speaker.twitter
        if (url.isEmpty()) {
            twitter.visibility = View.GONE
        } else {
            twitter.visibility = View.VISIBLE

            twitter.setOnClickListener {
                val url = "https://twitter.com/" + url.replace("@", "")

                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
                context?.startActivity(intent)

                analytics.onSpeakerEvent(Analytics.SPEAKER_TWITTER, speaker)
            }
        }


        val body = speaker.description

        if (body.isNotBlank()) {
            empty.visibility = View.GONE
            description.text = body
        } else {
            empty.visibility = View.VISIBLE
        }

        val colours = context!!.resources.getStringArray(R.array.colors)
        val color = Color.parseColor(colours[speaker.id % colours.size])

        app_bar.setBackgroundColor(color)

        database.getEventsForSpeaker(speaker).observe(this, Observer { list ->

            events_header.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE

            list.forEach {
                events.addView(EventView(context!!, it, EventView.DISPLAY_MODE_MIN))
            }
        })

        analytics.onSpeakerEvent(Analytics.SPEAKER_VIEW, speaker)
    }
}