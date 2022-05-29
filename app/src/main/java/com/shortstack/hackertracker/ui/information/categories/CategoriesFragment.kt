package com.shortstack.hackertracker.ui.information.categories

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.ListFragment
import com.shortstack.hackertracker.ui.activities.MainActivity
import java.util.*

class CategoriesFragment : ListFragment<Type>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel =
            ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java]

        viewModel.types.observe(viewLifecycleOwner) {
            val resource = Resource(
                it.status,
                it.data?.filter { !it.isBookmark }?.sortedBy { it.shortName.lowercase(Locale.getDefault()) },
                it.message
            )
            onResource(resource)
        }
    }

    override fun getPageTitle(): String {
        return "Categories"
    }

    companion object {
        fun newInstance() = CategoriesFragment()
    }
}



