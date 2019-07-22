package com.shortstack.hackertracker.ui.information.speakers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.fragment_list.*

class SpeakersFragment : Fragment() {

    companion object {
        fun newInstance() = SpeakersFragment()
    }

    private val adapter = SpeakerAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter

        val viewModel = ViewModelProviders.of(this).get(SpeakersViewModel::class.java)
        viewModel.speakers.observe(this, Observer {
            adapter.setSpeakers(it)
        })
    }
}