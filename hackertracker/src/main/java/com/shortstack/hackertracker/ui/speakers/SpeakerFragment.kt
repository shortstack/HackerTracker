package com.shortstack.hackertracker.ui.speakers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Speaker
import com.shortstack.hackertracker.views.EventView
import kotlinx.android.synthetic.main.fragment_speakers.*
import javax.inject.Inject

/**
 * Created by Chris on 7/31/2018.
 */
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

    @Inject
    lateinit var database: DatabaseManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_speakers, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        App.application.component.inject(this)

        val context = context ?: return

        val speaker = arguments?.getParcelable(EXTRA_SPEAKER) as? Speaker
        speaker?.let {
            description.text = it.description

            val eventsForSpeaker = database.getEventsForSpeaker(it.id)

            eventsForSpeaker.forEach {
                events.addView(EventView(context, it))
            }
        }


    }
}