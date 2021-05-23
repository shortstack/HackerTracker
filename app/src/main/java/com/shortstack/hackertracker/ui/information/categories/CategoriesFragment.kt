package com.shortstack.hackertracker.ui.information.categories

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.ListFragment
import com.shortstack.hackertracker.ui.activities.MainActivity

class CategoriesFragment : ListFragment<Type>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel =
            ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java]

        viewModel.types.observe(viewLifecycleOwner, Observer {
            val resource = Resource(
                it.status,
                it.data?.filter { !it.isBookmark }?.sortedBy { it.shortName.toLowerCase() },
                it.message
            )
            onResource(resource)
        })
    }

    companion object {
        fun newInstance() = CategoriesFragment()
    }
}



