package com.shortstack.hackertracker.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Conference
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.models.local.Type
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ScheduleViewModel(type: Type? = null) : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val schedule: LiveData<Resource<List<Event>>>
        get() = contents

    val conference: LiveData<Conference>
        get() = database.conference

    private val contents = Transformations.switchMap(database.conference) { id ->
        val result = MediatorLiveData<Resource<List<Event>>>()

        if (id == null) {
            result.value = Resource.init(null)
            return@switchMap result
        }

        result.value = Resource.loading(null)

        val events = database.getEvents(id)

        val types = if (type != null) {
            val result = MediatorLiveData<List<Type>>()
            result.value = listOf(type)
            result
        } else {
            database.getTypes(id)
        }

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

        val requireBookmark = types.firstOrNull { it.isBookmark }?.isSelected ?: false
        val filter = types.filter { !it.isBookmark && it.isSelected }
        if (!requireBookmark && filter.isEmpty())
            return events

        if (requireBookmark && filter.isEmpty())
            return events.filter { it.isBookmarked }

        return events.filter { event -> isShown(event, requireBookmark, filter) }
    }

    private fun isShown(event: Event, requireBookmark: Boolean, filter: List<Type>): Boolean {
        val bookmark = if (requireBookmark) {
            event.isBookmarked
        } else {
            true
        }


        return bookmark && filter.find { it.id == event.type.id }?.isSelected == true

    }
}