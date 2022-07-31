package com.advice.schedule.ui.information.vendors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.advice.schedule.Response
import com.advice.schedule.models.local.Vendor
import com.advice.schedule.ui.HackerTrackerViewModel
import com.advice.schedule.ui.ListAdapter
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VendorsFragment : Fragment() {

    private val viewModel by sharedViewModel<HackerTrackerViewModel>()

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
        binding.toolbar.title = getString(R.string.partners_vendors)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.vendors.observe(viewLifecycleOwner) {
            onResource(it)
        }
    }

    private fun onResource(resource: Response<List<Vendor>>) {
        when (resource) {
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

                if (resource.data.isNotEmpty()) {
                    adapter.addAllAndNotify(resource.data)
                    hideViews()
                } else {
                    showEmptyView()
                }
            }
            is Response.Error -> {
                setProgressIndicator(active = false)
                showErrorView(resource.exception.message)
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
        binding.emptyView.showError("Vendors not found")
    }

    private fun showErrorView(message: String?) {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showError(message)
    }

    private fun hideViews() {
        binding.emptyView.visibility = View.GONE
    }

    companion object {
        fun newInstance() = VendorsFragment()
    }
}



