package com.shortstack.hackertracker.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.databinding.FilterFragmentBinding
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FilterFragment : Fragment() {

    private val viewModel by sharedViewModel<HackerTrackerViewModel>()

    private var _binding: FilterFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.types.observe(viewLifecycleOwner) {
            binding.filters.setTypes(it.data)
            val hasFilters = it.data?.any { it.isSelected }

        }

        binding.filters.setOnTypeClickListener {
            viewModel.toggleFilter(it)
        }

        binding.filters.setOnClearListener {
            viewModel.clearFilters()
        }
    }
}