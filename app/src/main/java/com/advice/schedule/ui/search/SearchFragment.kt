package com.advice.schedule.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.hideKeyboard
import com.advice.schedule.showKeyboard
import com.advice.schedule.ui.HackerTrackerViewModel
import com.advice.schedule.ui.activities.MainActivity
import com.advice.schedule.ui.search.SearchAdapter.State.*
import com.shortstack.hackertracker.databinding.FragmentSearchBinding

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private val viewModel by lazy { ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java] }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val adapter = SearchAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.loadingProgress.visibility = View.GONE
        binding.emptyView.showDefault()

        binding.list.adapter = adapter

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().hideKeyboard()
            requireActivity().onBackPressed()
        }

        binding.search.onActionViewExpanded()

        binding.search.setOnCloseListener {
            requireActivity().hideKeyboard()
            requireActivity().onBackPressed()
            return@setOnCloseListener true
        }

        binding.search.setOnQueryTextListener(this)

        viewModel.search.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)

            when (adapter.state) {
                INIT -> binding.emptyView.showDefault()
                RESULTS -> {
                    binding.emptyView.hide()
                    binding.list.layoutManager?.smoothScrollToPosition(
                        binding.list,
                        RecyclerView.State(),
                        0
                    )
                }
                EMPTY -> binding.emptyView.showNoResults(adapter.query)
            }
        })

        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    requireActivity().hideKeyboard()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        requireActivity().showKeyboard()
    }

    override fun onQueryTextSubmit(query: String?) = true

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.query = newText
        viewModel.onQueryTextChange(newText)
        return false
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}