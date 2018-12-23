package com.shortstack.hackertracker.ui.contests

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.ui.ListFragment
import com.shortstack.hackertracker.ui.schedule.renderers.EventRenderer
import com.shortstack.hackertracker.ui.schedule.renderers.RelativeDayRender
import com.shortstack.hackertracker.ui.schedule.renderers.RelativeTimeRenderer

/**
 * Created by Chris on 05/08/18.
 */
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


        elements.groupBy { it.date }.forEach {
            result.add(Day(it.key))

            it.value.groupBy { it.start }.forEach {
                result.add(Time(it.key))

                if (it.value.isNotEmpty()) {
                    val group = it.value.sortedWith(compareBy({ it.type.name }, { it.location.name }))
                    result.addAll(group)
                }
            }
        }

        return result
    }

    override fun initAdapter(): RendererAdapter<Any> {
        return RendererBuilder.create<Any>()
                .bind(FirebaseEvent::class.java, EventRenderer())
                .bind(Day::class.java, RelativeDayRender())
                .bind(Time::class.java, RelativeTimeRenderer())
                .build()
    }
}