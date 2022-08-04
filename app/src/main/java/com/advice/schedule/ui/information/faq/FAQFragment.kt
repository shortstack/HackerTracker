package com.advice.schedule.ui.information.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.advice.schedule.Response
import com.advice.schedule.models.local.FAQAnswer
import com.advice.schedule.models.local.FAQQuestion
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FAQFragment : Fragment() {

    private val viewModel by sharedViewModel<FAQViewModel>()

    private var _binding: FragmentRecyclerviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FAQAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FAQAdapter {
            viewModel.toggle(it)
        }

        binding.list.adapter = adapter
        binding.toolbar.title = getString(R.string.faq)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.getFAQ().observe(viewLifecycleOwner) {
            onResource(it)
        }
    }

    private fun onResource(resource: Response<List<Any>>) {
        when (resource) {
            is Response.Init -> {
                setProgressIndicator(active = false)
                showInitView()
            }
            is Response.Loading -> {
                setProgressIndicator(active = true)
                adapter.submitList(emptyList())
                hideViews()
            }
            is Response.Success -> {
                setProgressIndicator(active = false)
                adapter.submitList(emptyList())

                if (resource.data.isNotEmpty()) {
                    val list = resource.data
                        .filter { it is FAQQuestion || (it is FAQAnswer && it.isExpanded) }
                    adapter.submitList(list)
                    hideViews()
                } else {
                    showEmptyView()
                }
            }
            is Response.Error -> {
                setProgressIndicator(active = false)
                showErrorView(resource.exception.message)
            }
        }
    }

    private fun setProgressIndicator(active: Boolean) {
        binding.loadingProgress.visibility = if (active) View.VISIBLE else View.GONE
    }

    private fun showInitView() {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showDefault()
    }

    private fun showEmptyView() {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showError("FAQ not found")
    }

    private fun showErrorView(message: String?) {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showError(message)
    }

    private fun hideViews() {
        binding.emptyView.visibility = View.GONE
    }


    companion object {
        fun newInstance() = FAQFragment()
    }
}
