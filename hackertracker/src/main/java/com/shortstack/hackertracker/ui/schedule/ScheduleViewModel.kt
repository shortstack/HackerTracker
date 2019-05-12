package com.shortstack.hackertracker.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.models.local.Event
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ScheduleViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val schedule: LiveData<Resource<List<Event>>>
        get() = contents

    private val events = database.getSchedule()
    private val types = database.getScheduleTypes()

    private val contents = Transformations.switchMap(database.conference) { id ->
        val result = MediatorLiveData<Resource<List<Event>>>()

        result.value = Resource.loading(null)

        result.addSource(events) {
            val types = types.value ?: emptyList()
            result.value = Resource.success(getSchedule(it, types))
        }

        result.addSource(types) { types ->
            val events = events.value ?: return@addSource
            result.value = Resource.success(getSchedule(events, types))
        }

        return@switchMap result
    }

    private fun getSchedule(events: List<Event>, types: List<Type>): List<Event> {
        if (types.isEmpty())
            return events

        val filter = types.filter { it.isSelected }
        if (filter.isEmpty())
            return events

        return events.filter { event -> filter.find { it.id == event.type.id }?.isSelected == true }
    }
}