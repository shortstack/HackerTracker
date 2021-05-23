package com.shortstack.hackertracker.ui.information.speakers

import android.content.Intent
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
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.databinding.FragmentSpeakersBinding
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.views.EventView
import org.koin.android.ext.android.inject

class SpeakerFragment : Fragment() {

    private var _binding: FragmentSpeakersBinding? = null
    private val binding get() = _binding!!

    private val database: DatabaseManager by inject()
    private val analytics: Analytics by inject()

    private val viewModel by lazy { ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpeakersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val drawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_white_24dp)
        binding.toolbar.navigationIcon = drawable

        binding.toolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.popBackStack()
        }

        val speaker = arguments?.getParcelable(EXTRA_SPEAKER) as? Speaker

        viewModel.speakers.observe(viewLifecycleOwner, Observer {
            val target = it.data?.find { it.id == speaker?.id }
            if (target != null) {
                showSpeaker(target)
            }
        })
    }

    private fun showSpeaker(speaker: Speaker) {
        analytics.log("Viewing speaker ${speaker.name}")

        binding.collapsingToolbar.title = speaker.name
        binding.collapsingToolbar.subtitle = if (speaker.title.isEmpty()) {
            context?.getString(R.string.speaker_default_title)
        } else {
            speaker.title
        }

        binding.toolbar.menu.clear()

        val url = speaker.twitter
        if (url.isNotEmpty()) {
            binding.toolbar.inflateMenu(R.menu.speaker_twitter)

            binding.toolbar.setOnMenuItemClickListener {
                val url = "https://twitter.com/" + url.replace("@", "")

                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
                context?.startActivity(intent)

                analytics.onSpeakerEvent(Analytics.SPEAKER_TWITTER, speaker)
                true
            }
        }


        val body = speaker.description

        if (body.isNotBlank()) {
            // todo: binding.empty.visibility = View.GONE
            binding.description.text = body
        } else {
            // todo: binding.empty.visibility = View.VISIBLE
        }


        database.getEventsForSpeaker(speaker).observe(viewLifecycleOwner, Observer { list ->
            binding.eventsHeader.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            list.forEach {
                binding.events.addView(EventView(requireContext(), it, EventView.DISPLAY_MODE_MIN))
            }
        })

        analytics.onSpeakerEvent(Analytics.SPEAKER_VIEW, speaker)
    }

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
}