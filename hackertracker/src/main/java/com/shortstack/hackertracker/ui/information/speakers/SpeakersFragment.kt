package com.shortstack.hackertracker.ui.information.speakers

import android.os.Bundle
import androidx.lifecycle.Observer
import com.shortstack.hackertracker.models.firebase.FirebaseSpeaker
import com.shortstack.hackertracker.ui.ListFragment

class SpeakersFragment : ListFragment<FirebaseSpeaker>() {

    companion object {
        fun newInstance() = SpeakersFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getViewModel<SpeakersViewModel>().speakers.observe(this, Observer {
            onResource(it)
        })
    }
}