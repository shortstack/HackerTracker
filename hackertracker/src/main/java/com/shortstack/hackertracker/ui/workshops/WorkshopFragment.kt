package com.shortstack.hackertracker.ui.workshops

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.ui.ListFragment
import com.shortstack.hackertracker.ui.schedule.renderers.EventRenderer
import com.shortstack.hackertracker.ui.schedule.renderers.RelativeDayRender
import com.shortstack.hackertracker.ui.schedule.renderers.RelativeTimeRenderer

/**
 * Created by Chris on 05/08/18.
 */
class WorkshopFragment : ListFragment<Any>() {

    companion object {
        fun newInstance() = WorkshopFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel<WorkshopViewModel>().workshops.observe(this, Observer {
            if (it.data != null) {
                val elements = getFormattedElements(it.data)
                onResource(Resource.success(elements))
            } else {
                onResource(it)
            }
        })
    }

    private fun getFormattedElements(elements: List<DatabaseEvent>): ArrayList<Any> {
        val result = ArrayList<Any>()


        elements.groupBy { it.event.date }.forEach {
            result.add(Day(it.key))

            it.value.groupBy { it.event.begin }.forEach {
                result.add(Time(it.key))

                if (it.value.isNotEmpty()) {
                    val group = it.value.sortedWith(compareBy({ it.type.firstOrNull()?.name }, { it.location.firstOrNull()?.name }))
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