package com.shortstack.hackertracker.ui.information.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.databinding.FragmentInfoBinding
import org.koin.android.ext.android.inject

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private val database: DatabaseManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        database.conference.observe(viewLifecycleOwner, Observer {
            binding.conduct.setText(it.conduct)
        })
    }

    companion object {
        fun newInstance() = InfoFragment()
    }
}