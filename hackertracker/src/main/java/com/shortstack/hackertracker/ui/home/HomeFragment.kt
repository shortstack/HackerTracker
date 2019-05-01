package com.shortstack.hackertracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val adapter = HomeAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.adapter = adapter
        val manager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        manager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> 2
                    2, 3, 5, 6 -> 1
                    else -> 2
                }
            }
        }

        list.layoutManager = manager

        val viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        // TODO: Use ViewModel.

        loading_progress.visibility = View.GONE
    }
}
