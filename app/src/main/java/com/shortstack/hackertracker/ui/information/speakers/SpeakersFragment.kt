package com.shortstack.hackertracker.ui.information.speakers

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.firebase.FirebaseSpeaker
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.ListFragment
import com.shortstack.hackertracker.ui.activities.MainActivity

class SpeakersFragment : ListFragment<FirebaseSpeaker>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel =
            ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java]

        viewModel.speakers.observe(context as MainActivity) {
            onResource(it)
        }
    }

    override fun getPageTitle(): String {
        return getString(R.string.speakers)
    }

    companion object {
        fun newInstance() = SpeakersFragment()
    }
}