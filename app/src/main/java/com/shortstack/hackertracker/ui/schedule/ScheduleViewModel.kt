package com.shortstack.hackertracker.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.Response
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.models.local.Location
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.models.local.Type
import org.koin.core.KoinComponent
import org.koin.core.inject

class ScheduleViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val events = Transformations.switchMap(database.conference) {
        val result = MediatorLiveData<Response<List<Event>>>()

        if (it == null) {
            result.value = Response.Init
        } else {
            result.addSource(database.getSchedule()) {
                result.value = Response.Success(it)
            }
        }

        return@switchMap result
    }

    private val types = Transformations.switchMap(database.conference) {
        val result = MediatorLiveData<Response<List<Type>>>()

        if (it == null) {
            result.value = Response.Init
        } else {
            result.addSource(database.getTypes(it)) {
                result.value = Response.Success(it)
            }
        }
        return@switchMap result
    }

    fun getSchedule(location: Location): LiveData<Response<List<Event>>> {
        val result = MediatorLiveData<Response<List<Event>>>()

        result.addSource(events) {
            val events = (it as? Response.Success)?.data
                ?.filter { it.location.name == location.name } ?: emptyList()
            result.value = Response.Success(events)
        }

        return result
    }

    fun getSchedule(type: Type): LiveData<Response<List<Event>>> {
        val result = MediatorLiveData<Response<List<Event>>>()

        result.addSource(events) {
            val events = (it as? Response.Success)?.data
                ?.filter { it.types.any { it.id == type.id } } ?: emptyList()
            result.value = Response.Success(events)
        }

        return result
    }

    fun getSchedule(speaker: Speaker): LiveData<Response<List<Event>>> {
        val result = MediatorLiveData<Response<List<Event>>>()

        result.addSource(events) {
            val events = (it as? Response.Success)?.data
                ?.filter { it.speakers.any { it.id == speaker.id } } ?: emptyList()
            result.value = Response.Success(events)
        }

        return result
    }

    fun getSchedule(): LiveData<Response<List<Event>>> {
        val result = MediatorLiveData<Response<List<Event>>>()

        result.addSource(events) {
            val events = (it as? Response.Success)?.data ?: emptyList()
            val types = (types.value as? Response.Success)?.data ?: return@addSource
            result.value = Response.Success(getSchedule(events, types))
        }

        result.addSource(types) {
            val events = (events.value as? Response.Success)?.data ?: return@addSource
            val types = (it as? Response.Success)?.data ?: emptyList()
            result.value = Response.Success(getSchedule(events, types))
        }

        return result
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

        return bookmark &&
                event.types.any { t -> filter.find { it.id == t.id }?.isSelected == true }
    }
}