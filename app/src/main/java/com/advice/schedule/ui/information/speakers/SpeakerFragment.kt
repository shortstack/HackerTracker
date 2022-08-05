package com.advice.schedule.ui.information.speakers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.advice.schedule.Response
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.ui.HackerTrackerViewModel
import com.advice.schedule.ui.activities.MainActivity
import com.advice.schedule.utilities.Analytics
import com.advice.schedule.views.EventView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentSpeakersBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SpeakerFragment : Fragment() {

    private var _binding: FragmentSpeakersBinding? = null
    private val binding get() = _binding!!

    private val database: DatabaseManager by inject()
    private val analytics: Analytics by inject()

    private val viewModel by sharedViewModel<HackerTrackerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpeakersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val speaker =
            arguments?.getParcelable(EXTRA_SPEAKER) as? Speaker ?: error("speaker must not be null")

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.eventsContainer.setOnClickListener {
            (requireActivity() as MainActivity).showSchedule(speaker)
        }

        showSpeaker(speaker)

        viewModel.speakers.observe(viewLifecycleOwner) {
            when (it) {
                Response.Init -> {

                }
                Response.Loading -> {

                }
                is Response.Success -> {
                    val target = it.data.find { it.id == speaker.id }
                    if (target != null) {
                        showSpeaker(target)
                    }
                }
                is Response.Error -> {}
            }
        }

        database.getEventsForSpeaker(speaker).observe(viewLifecycleOwner) { list ->
            binding.eventsHeader.isVisible = list.isNotEmpty()
            list.forEach {
                binding.events.addView(EventView(requireContext(), it, EventView.DISPLAY_MODE_MIN))
            }
        }
        analytics.log("Viewing speaker ${speaker.name}")
        analytics.onSpeakerEvent(Analytics.SPEAKER_VIEW, speaker)
    }

    private fun showSpeaker(speaker: Speaker) = with(binding) {
        speakerName.text = speaker.name

        titleContainer.isVisible = speaker.title.isNotEmpty()
        speakerTitle.text = speaker.title

        toolbar.menu.clear()

        if (speaker.twitter.isNotEmpty()) {
            toolbar.inflateMenu(R.menu.speaker_twitter)
            toolbar.setOnMenuItemClickListener {
                openTwitter(speaker.twitter)
                analytics.onSpeakerEvent(Analytics.SPEAKER_TWITTER, speaker)
                true
            }
        }

        description.isVisible = speaker.description.isNotBlank()
        description.text = speaker.description
    }

    private fun openTwitter(url: String) {
        try {
            val url = "https://twitter.com/" + url.replace("@", "")
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
            context?.startActivity(intent)
        } catch (ex: Exception) {
            // do nothing
        }
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