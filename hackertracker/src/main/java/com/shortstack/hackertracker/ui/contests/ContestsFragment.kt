package com.shortstack.hackertracker.ui.contests

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.firebase.FirebaseEvent
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.ui.ListFragment

class ContestsFragment : ListFragment<Any>() {

    companion object {
        fun newInstance() = ContestsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel<ContestsViewModel>().contests.observe(this, Observer {
            if (it.data != null) {
                val elements = getFormattedElements(it.data)
                onResource(Resource.success(elements))
            } else {
                onResource(it)
            }
        })
    }

    private fun getFormattedElements(elements: List<FirebaseEvent>): ArrayList<Any> {
        val result = ArrayList<Any>()


        elements.groupBy { it.date }.toSortedMap().forEach {
            result.add(Day(it.key))

            it.value.groupBy { it.start }.toSortedMap().forEach {
                result.add(Time(it.key))

                if (it.value.isNotEmpty()) {
                    val group = it.value.sortedWith(compareBy({ it.type.name }, { it.location.name }))
                    result.addAll(group)
                }
            }
        }

        return result
    }
}