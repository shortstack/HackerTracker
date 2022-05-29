package com.shortstack.hackertracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding

abstract class ListFragment<T> : Fragment() {

    private var _binding: FragmentRecyclerviewBinding? = null
    private val binding get() = _binding!!

    private val adapter = ListAdapter()

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
        binding.list.adapter = adapter
        binding.toolbar.title = getPageTitle()
    }

    abstract fun getPageTitle(): String

    inline fun <reified J : ViewModel> getViewModel(): J =
        ViewModelProviders.of(this).get(J::class.java)

    fun onResource(resource: Resource<List<Any>>?) {
        when (resource?.status) {
            Status.SUCCESS -> {
                setProgressIndicator(active = false)
                adapter.clearAndNotify()

                if (resource.data?.isNotEmpty() == true) {
                    adapter.addAllAndNotify(resource.data)
                    hideViews()
                } else {
                    showEmptyView()
                }
            }
            Status.ERROR -> {
                setProgressIndicator(active = false)
                showErrorView(resource.message)
            }
            Status.LOADING -> {
                setProgressIndicator(active = true)
                adapter.clearAndNotify()
                hideViews()
            }
            Status.NOT_INITIALIZED -> {
                setProgressIndicator(active = false)
                showInitView()
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
        binding.emptyView.showNoResults()
    }

    private fun showErrorView(message: String?) {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showError(message)
    }

    private fun hideViews() {
        binding.emptyView.visibility = View.GONE
    }
}