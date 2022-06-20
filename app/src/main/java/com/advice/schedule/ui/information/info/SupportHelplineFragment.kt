package com.advice.schedule.ui.information.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.databinding.FragmentSupportHotlineBinding

class SupportHelplineFragment : Fragment() {

    private var _binding: FragmentSupportHotlineBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportHotlineBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = SupportHelplineFragment()
    }
}