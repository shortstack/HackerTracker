package com.shortstack.hackertracker.ui.information.speakers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Response
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding
import com.shortstack.hackertracker.ui.ListAdapter
import com.shortstack.hackertracker.ui.activities.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpeakersFragment : Fragment() {

    private val viewModel by viewModel<SpeakersViewModel>()

    private var _binding: FragmentRecyclerviewBinding? = null
    private val binding get() = _binding!!

    private val adapter = ListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.adapter = adapter
        binding.toolbar.title = getString(R.string.speakers)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.getSpeakers().observe(context as MainActivity) { response ->
            when (response) {
                is Response.Init -> {
                    setProgressIndicator(active = false)
                    showInitView()
                }
                is Response.Loading -> {
                    setProgressIndicator(active = true)
                    adapter.clearAndNotify()
                    hideViews()
                }
                is Response.Success -> {
                    setProgressIndicator(active = false)
                    adapter.clearAndNotify()

                    if (response.data.isNotEmpty()) {
                        adapter.addAllAndNotify(response.data)
                        hideViews()
                    } else {
                        showEmptyView()
                    }
                }
                is Response.Error -> {
                    setProgressIndicator(active = false)
                    showErrorView(response.exception.message)
                }
            }
        }
    }

    private fun setProgressIndicator(active: Boolean) {
        binding.loadingProgress.visibility = if (active) View.VISIBLE else View.GONE
    }

    private fun showInitView() {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showDefault()
    }

    private fun showEmptyView() {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showError("Speakers not found")
    }

    private fun showErrorView(message: String?) {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showError(message)
    }

    private fun hideViews() {
        binding.emptyView.visibility = View.GONE
    }

    companion object {
        fun newInstance() = SpeakersFragment()
    }
}