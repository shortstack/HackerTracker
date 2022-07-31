package com.advice.schedule.ui.information.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.Response
import com.advice.schedule.views.LocationContainer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding
import com.shortstack.hackertracker.databinding.LocationRowBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationsFragment : Fragment() {

    private val viewModel by viewModel<LocationsViewModel>()

    private var _binding: FragmentRecyclerviewBinding? = null
    private val binding get() = _binding!!


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

        binding.toolbar.title = getString(R.string.locations_schedule)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val adapter = LocationsAdapter { location ->
            viewModel.toggle(location)
        }

        binding.list.adapter = adapter

        viewModel.getLocations().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Init -> {
                    setProgressIndicator(active = false)
                    showInitView()
                }
                is Response.Loading -> {
                    setProgressIndicator(active = true)
                    adapter.submitList(emptyList())
                    hideViews()
                }
                is Response.Success -> {
                    setProgressIndicator(active = false)
                    adapter.submitList(emptyList())

                    if (response.data.isNotEmpty()) {
                        adapter.submitList(response.data)
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
        fun newInstance() = LocationsFragment()
    }
}

class LocationsAdapter(private val onClickListener: (LocationContainer) -> Unit) : ListAdapter<LocationContainer, LocationViewHolder>(DIFF_UTILS) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.render(getItem(position), onClickListener)
    }

    override fun submitList(list: List<LocationContainer>?) {
        super.submitList(list?.filter { it.isExpanded })
    }

    companion object {
        private val DIFF_UTILS = object : DiffUtil.ItemCallback<LocationContainer>() {
            override fun areItemsTheSame(oldItem: LocationContainer, newItem: LocationContainer): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: LocationContainer, newItem: LocationContainer): Boolean {
                return oldItem.status == newItem.status
            }
        }
    }
}

class LocationViewHolder(private val binding: LocationRowBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun inflate(parent: ViewGroup): LocationViewHolder {
            val binding =
                LocationRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LocationViewHolder(binding)
        }
    }

    fun render(location: LocationContainer, onClickListener: (LocationContainer) -> Unit) {
        binding.location.setLocation(location, onClickListener)
    }
}