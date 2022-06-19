package com.shortstack.hackertracker.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shortstack.hackertracker.databinding.FragmentMapBinding
import java.io.File

class MapFragment : androidx.fragment.app.Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val file = arguments?.getSerializable(ARG_PDF) as? File

        if (file == null) {
            binding.progress.visibility = View.VISIBLE
        } else {
            binding.progress.visibility = View.VISIBLE

            binding.viewer.fromFile(file).onLoad {
                binding.progress.visibility = View.GONE
            }.load()
        }
    }

    override fun onDestroyView() {
        binding.viewer.recycle()
        super.onDestroyView()
    }

    companion object {
        private const val ARG_PDF = "PDF"

        fun newInstance(file: File?): MapFragment {
            val fragment = MapFragment()

            val bundle = Bundle()
            bundle.putSerializable(ARG_PDF, file)
            fragment.arguments = bundle

            return fragment
        }
    }
}