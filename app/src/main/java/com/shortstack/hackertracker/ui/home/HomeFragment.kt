package com.shortstack.hackertracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shortstack.hackertracker.databinding.FragmentHomeBinding
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity

class HomeFragment : Fragment() {

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

        binding.toolbar.setNavigationOnClickListener {
            (context as MainActivity).openNavDrawer()
        }

        val viewModel = ViewModelProvider(requireActivity())[HackerTrackerViewModel::class.java]
        viewModel.home.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                adapter.setElements(it.data)
            }
        })

        binding.loadingProgress.visibility = View.GONE
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
