package com.advice.schedule.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.advice.schedule.Response
import com.advice.schedule.dObj
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.schedule.models.local.Event
import com.advice.schedule.models.local.Location
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.models.local.Type
import org.koin.core.KoinComponent
import org.koin.core.inject

class ScheduleViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val events = MediatorLiveData<Response<List<Event>>>()
    private val types = MediatorLiveData<Response<List<FirebaseTagType>>>()

    init {
        events.addSource(database.conference) {
            var isFirst = true

            if (it == null) {
                isFirst = true
                events.value = Response.Init
                types.value = Response.Init
            } else {
                types.addSource(database.getTags(it)) {
                    types.value = Response.Success(it)

                    if (isFirst) {
                        isFirst = false
                        events.addSource(database.getSchedule()) {
                            events.value = Response.Success(it)
                        }
                    }
                }
            }
        }
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

    fun getSchedule(type: FirebaseTag): LiveData<Response<List<Event>>> {
        val result = MediatorLiveData<Response<List<Event>>>()

        result.addSource(events) {
            val events = (it as? Response.Success)?.data
                ?.filter { /*(type.isBookmark && it.isBookmarked) ||*/ (it.types.any { it.id == type.id }) }
                ?: emptyList()
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
            val types = types.value?.dObj as? List<FirebaseTagType> ?: emptyList()
            val data = getSchedule(events, types)
            result.value = Response.Success(data)
        }

        result.addSource(types) {
            val events = (events.value as? Response.Success)?.data ?: return@addSource
            val types = (it as? Response.Success)?.data ?: emptyList()
            val data = getSchedule(events, types)
            result.value = Response.Success(data)
        }

        return result
    }

    private fun getSchedule(events: List<Event>, types: List<FirebaseTagType>): List<Event> {
        if (types.isEmpty())
            return events

        val requireBookmark = false//types.firstOrNull { it.isBookmark }?.isSelected ?: false
        val filter = types.flatMap { it.tags }.filter { /*!it.isBookmark && */it.isSelected }
        if (!requireBookmark && filter.isEmpty())
            return events

        if (requireBookmark && filter.isEmpty())
            return events.filter { it.isBookmarked }

        val filter1 = events.filter { event -> isShown(event, requireBookmark, filter) }
        return filter1
    }

    private fun isShown(event: Event, requireBookmark: Boolean, filter: List<FirebaseTag>): Boolean {
        val bookmark = if (requireBookmark) {
            event.isBookmarked
        } else {
            true
        }

        // just in case
        if (event.types.isEmpty())
            return true

        return bookmark && event.types.any { t -> filter.find { it.id == t.id }?.isSelected == true }
    }
}