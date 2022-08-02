package com.advice.schedule.ui.information.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.advice.schedule.database.DatabaseManager
import com.shortstack.hackertracker.databinding.FragmentCodeOfConductBinding
import org.koin.android.ext.android.inject

class CodeOfConductFragment : Fragment() {

    private var _binding: FragmentCodeOfConductBinding? = null
    private val binding get() = _binding!!

    private val database: DatabaseManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCodeOfConductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        database.conference.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.conduct.setText(it.conduct)
            }
        }
    }

    companion object {
        fun newInstance() = CodeOfConductFragment()
    }
}