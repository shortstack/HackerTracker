package com.advice.schedule.ui.home

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.advice.schedule.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

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

        viewModel.home.observe(viewLifecycleOwner) {
            if (it.data != null) {
                adapter.setElements(it.data)
            }
        }

        viewModel.conference.observe(viewLifecycleOwner) {
            if (it.data != null) {
                binding.title.text = it.data.name
                startCountdownTimer(it.data.startDate)
            }
        }

        binding.header.setOnClickListener {
            showConferenceChooseDialog()
        }

        binding.loadingProgress.visibility = View.GONE
    }

    private fun showConferenceChooseDialog() {
        val conferences = viewModel.conferences.value ?: emptyList()
        val element = viewModel.conference.value?.data ?: return
        val selected = conferences.indexOf(element)

        val items = conferences.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
            .setTitle(getString(R.string.choose_conference))
            .setSingleChoiceItems(items, selected) { dialog, which ->
                viewModel.changeConference(conferences[which])
                dialog.dismiss()
            }.show()
    }

    private var timer: CountDownTimer? = null

    private fun startCountdownTimer(startDate: Date) {
        val now = startDate.time - Date().time
        binding.skull.setCountdown(now)

        timer?.cancel()
        timer = null

        timer = object : CountDownTimer(now, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.skull.setCountdown(millisUntilFinished)
            }

            override fun onFinish() {
                binding.skull.setCountdown(0)
            }
        }
        timer?.start()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
