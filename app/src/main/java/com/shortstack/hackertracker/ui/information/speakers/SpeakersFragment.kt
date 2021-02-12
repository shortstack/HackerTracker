package com.shortstack.hackertracker.ui.information.speakers

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shortstack.hackertracker.models.firebase.FirebaseSpeaker
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.ListFragment
import com.shortstack.hackertracker.ui.activities.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpeakersFragment : ListFragment<FirebaseSpeaker>() {

    companion object {
        fun newInstance() = SpeakersFragment()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java]

        viewModel.speakers.observe(context as MainActivity, Observer {
            onResource(it)
        })
    }
}