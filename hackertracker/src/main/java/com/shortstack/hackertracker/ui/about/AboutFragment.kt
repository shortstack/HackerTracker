package com.shortstack.hackertracker.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import com.shortstack.hackertracker.views.EventView
import com.shortstack.hackertracker.views.SpeakerView
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject

/**
 * Created by Chris on 05/08/18.
 */
class AboutFragment : Fragment() {

    companion object {
        private const val HACKER_TRACKER_EVENT_ID = 26016
        private const val CLICK_MAX = 10

        fun newInstance() = AboutFragment()
    }

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var preferences: SharedPreferencesUtil

    private var count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        App.application.component.inject(this)

        val context = context ?: return

        version.text = BuildConfig.VERSION_NAME

        val event = database.getEvent(HACKER_TRACKER_EVENT_ID)
        if (event != null)
            event_container.addView(EventView(context, event))

        val members = database.getSpeakers(HACKER_TRACKER_EVENT_ID)
        members.forEach {
            members_container.addView(SpeakerView(context, it))
        }

        version.setOnClickListener {
            if (preferences.isHacker) {
                Toast.makeText(context, "You are already a hacker", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            count++

            if (count == CLICK_MAX) {
                preferences.isHacker = true
                Toast.makeText(context, "You are now a hacker", Toast.LENGTH_SHORT).show()
            } else if (count > 0) {
                Toast.makeText(context, "You are ${CLICK_MAX - count} clicks away", Toast.LENGTH_SHORT).show()
            }
        }
    }
}