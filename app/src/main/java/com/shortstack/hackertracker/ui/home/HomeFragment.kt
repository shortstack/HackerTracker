package com.shortstack.hackertracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.shortstack.hackertracker.databinding.FragmentHomeBinding
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : Fragment() {

    private val viewModel by sharedViewModel<HackerTrackerViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val adapter = HomeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(context)

        viewModel.home.observe(viewLifecycleOwner, {
            if (it.data != null) {
                adapter.setElements(it.data)
            }
        })

        viewModel.conference.observe(viewLifecycleOwner, {
            binding.title.text = it.data?.name
        })

        binding.loadingProgress.visibility = View.GONE
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
