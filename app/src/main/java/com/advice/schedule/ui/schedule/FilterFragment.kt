package com.advice.schedule.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.advice.schedule.models.local.Type
import com.advice.schedule.ui.HackerTrackerViewModel
import com.advice.schedule.ui.activities.MainActivity
import com.advice.schedule.views.FilterAdapter
import com.shortstack.hackertracker.databinding.FilterFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FilterFragment : Fragment() {

    private val viewModel by sharedViewModel<HackerTrackerViewModel>()

    private var _binding: FilterFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter: FilterAdapter = FilterAdapter({
        viewModel.toggleFilter(it)
    }, {
        (requireActivity() as MainActivity).showSchedule(it)
    })

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

        binding.list.adapter = adapter
        binding.toolbar.setOnMenuItemClickListener {
            viewModel.clearFilters()
            true
        }

        binding.hint.isVisible = true
        binding.hint.setOnCloseListener {
            // todo: save it as hidden
        }

        viewModel.types.observe(viewLifecycleOwner) {
            setTypes(it.data)
            val hasFilters = it.data?.any { it.isSelected }

        }
    }

    private fun setTypes(types: List<Type>?) {
        if (types != null) {
            val collection = ArrayList<Any>()

            types.find { it.isBookmark }?.let {
                collection.add(it)
            }

//            collection.add(context.getString(R.string.types))

            val elements = types.filter { !it.isBookmark && !it.isVillage && !it.isWorkshop }
                .sortedBy { it.shortName }
            collection.addAll(elements)

//            collection.add(context.getString(R.string.villages))

            val villages = types.filter { it.isVillage }.sortedBy { it.shortName }
            collection.addAll(villages)

//            collection.add(context.getString(R.string.workshops))

            val workshops = types.filter { it.isWorkshop }.sortedBy { it.shortName }
            collection.addAll(workshops)

            adapter.setElements(collection)
        }
    }
}