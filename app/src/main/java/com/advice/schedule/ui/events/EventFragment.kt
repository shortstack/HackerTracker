package com.advice.schedule.ui.events

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.advice.schedule.Response
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.ReminderManager
import com.advice.schedule.models.local.Event
import com.advice.schedule.ui.activities.MainActivity
import com.advice.schedule.ui.information.locations.toContainer
import com.advice.schedule.ui.schedule.ScheduleViewModel
import com.advice.schedule.utilities.Analytics
import com.advice.schedule.utilities.TimeUtil
import com.advice.schedule.views.setStatus
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentEventBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private val analytics by inject<Analytics>()
    private val database by inject<DatabaseManager>()
    private val reminder by inject<ReminderManager>()

    private val viewModel by sharedViewModel<ScheduleViewModel>()

    private val speakersAdapter = EventDetailsAdapter()
    private val linksAdapter = EventDetailsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val event = arguments?.getParcelable<Event>(EXTRA_EVENT) ?: error("id must not be null")

        showEvent(event)

        viewModel.getSchedule().observe(viewLifecycleOwner) {
            if (it is Response.Success) {
                val target = it.data?.find { it.id == event.id }
                if (target != null) {
                    showEvent(target)
                }
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showEvent(event: Event) {
        analytics.log("Viewing event: ${event.title}")

        val body = event.description

        binding.speakers.adapter = speakersAdapter
        binding.links.adapter = linksAdapter

        binding.speakersHeader.isVisible = event.speakers.isNotEmpty()
        binding.speakersContainers.isVisible = event.speakers.isNotEmpty()
        speakersAdapter.setElements(emptyList(), event.speakers)
        binding.linksContainers.isVisible = event.urls.isNotEmpty()
        linksAdapter.setElements(event.urls.sortedBy { it.label.length }, emptyList())

        if (body.isNotBlank()) {
            // todo: binding.empty.visibility = View.GONE
            binding.description.text = body
        } else {
            // todo: binding.empty.visibility = View.VISIBLE
        }

        binding.toolbar.setOnMenuItemClickListener {
            onBookmarkClick(event)
            true
        }

        displayDescription(event)

        displayTypes(event)

        displayBookmark(event)


        analytics.onEventAction(Analytics.EVENT_VIEW, event)
    }

    private fun onBookmarkClick(event: Event) {
        event.isBookmarked = !event.isBookmarked

        database.updateBookmark(event)
        if (event.isBookmarked) {
            reminder.setReminder(event)
        } else {
            reminder.cancel(event)
        }

        val action =
            if (event.isBookmarked) Analytics.EVENT_BOOKMARK else Analytics.EVENT_UNBOOKMARK
        analytics.onEventAction(action, event)

        binding.toolbar.invalidate()
    }

    private fun displayBookmark(event: Event) {
        val isBookmarked = event.isBookmarked
        binding.toolbar.menu.clear()
        binding.toolbar.inflateMenu(if (isBookmarked) R.menu.event_bookmarked else R.menu.event_unbookmarked)
    }

    private fun displayDescription(event: Event) {
        binding.title.text = event.title
        binding.date.text = getFullTimeStamp(requireContext(), event).replace("\n", " ")
        val location = event.location.toContainer().apply {
            setStatus(getCurrentStatus())
        }
        binding.location.setLocation(location)

        binding.typeContainer.setOnClickListener {
            (requireActivity() as MainActivity).showSchedule(event.types.first())
        }

        binding.locationContainer.setOnClickListener {
            (requireActivity() as MainActivity).showSchedule(event.location)
        }
    }


    private fun getFullTimeStamp(context: Context, event: Event): String {
        val (begin, end) = getTimeStamp(context, event)
        val timestamp = TimeUtil.getDateStamp(event.start.toDate())

        return String.format(context.getString(R.string.timestamp_full), timestamp, begin, end)
    }

    private fun getTimeStamp(context: Context, event: Event): Pair<String, String> {
        val begin = TimeUtil.getTimeStamp(context, event.start.toDate())
        val end = TimeUtil.getTimeStamp(context, event.end.toDate())
        return Pair(begin, end)
    }


    private fun displayTypes(event: Event) {
        val type = event.types.first()
        binding.type1.render(type)
        binding.type1.visibility = View.VISIBLE

//        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.chip_background)?.mutate()
//
//        drawable?.setTint(Color.parseColor(type.color))
//
//        binding.typeContainer.background = drawable

        if (event.types.size > 1) {
            binding.type2.render(event.types.last())
            binding.type2.visibility = View.VISIBLE
        } else {
            binding.type2.visibility = View.GONE
        }
    }

    companion object {

        const val EXTRA_EVENT = "EXTRA_EVENT"

        fun newInstance(event: Event): EventFragment {
            val fragment = EventFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_EVENT, event)
            fragment.arguments = bundle

            return fragment
        }
    }
}