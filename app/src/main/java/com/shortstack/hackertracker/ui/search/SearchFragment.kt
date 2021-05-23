package com.shortstack.hackertracker.ui.search

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.databinding.FragmentSearchBinding
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.search.SearchAdapter.State.*


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
            hideKeyboard(context as MainActivity)
            (context as MainActivity).popBackStack()
        }

        binding.search.onActionViewExpanded()
        binding.search.setOnCloseListener {
            hideKeyboard(context as MainActivity)
            (context as MainActivity).popBackStack()
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
    }

    override fun onQueryTextSubmit(query: String?) = true

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.query = newText
        viewModel.onQueryTextChange(newText)
        return false
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus ?: View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}