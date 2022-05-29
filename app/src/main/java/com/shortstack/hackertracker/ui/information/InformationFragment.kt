package com.shortstack.hackertracker.ui.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.databinding.FragmentInformationBinding
import com.shortstack.hackertracker.replaceFragment
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.information.faq.FAQFragment
import com.shortstack.hackertracker.ui.information.info.CodeOfConductFragment
import com.shortstack.hackertracker.ui.information.info.SupportHelplineFragment
import com.shortstack.hackertracker.ui.information.info.WiFiFragment
import com.shortstack.hackertracker.ui.information.speakers.SpeakersFragment
import com.shortstack.hackertracker.ui.information.vendors.VendorsFragment
import org.koin.android.ext.android.inject

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private val database: DatabaseManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database.conference.observe(viewLifecycleOwner) {
            binding.codeOfConduct.isVisible = it.conduct != null
            binding.help.isVisible = it.code.contains("DEFCON")
            binding.wifi.isVisible = it.code.contains("DEFCON")
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.wifi.setOnClickListener {
            (requireActivity() as MainActivity).replaceFragment(
                WiFiFragment.newInstance(),
                R.id.container,
                backStack = true
            )
        }

        binding.help.setOnClickListener {
            (requireActivity() as MainActivity).replaceFragment(
                SupportHelplineFragment.newInstance(),
                R.id.container,
                backStack = true
            )
        }

        binding.codeOfConduct.setOnClickListener {
            (requireActivity() as MainActivity).replaceFragment(
                CodeOfConductFragment.newInstance(),
                R.id.container,
                backStack = true
            )
        }

        binding.faq.setOnClickListener {
            (requireActivity() as MainActivity).replaceFragment(
                FAQFragment.newInstance(),
                R.id.container,
                backStack = true
            )
        }

        binding.vendors.setOnClickListener {
            (requireActivity() as MainActivity).replaceFragment(
                VendorsFragment.newInstance(),
                R.id.container,
                backStack = true
            )
        }

        binding.speakers.setOnClickListener {
            (requireActivity() as MainActivity).replaceFragment(
                SpeakersFragment.newInstance(),
                R.id.container,
                backStack = true
            )
        }
    }

    companion object {
        fun newInstance(): InformationFragment = InformationFragment()
    }
}