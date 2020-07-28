package com.shortstack.hackertracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val adapter = HomeAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)

        toolbar.setNavigationOnClickListener {
            (context as MainActivity).openNavDrawer()
        }


        val viewModel = ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java]
        viewModel.home.observe(this, Observer {
            if (it.data != null)
                adapter.setElements(it.data)
        })

        loading_progress.visibility = View.GONE
    }
}
