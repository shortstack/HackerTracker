package com.shortstack.hackertracker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Location
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.models.local.Event
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SearchViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val query = MediatorLiveData<String>()

    val results: LiveData<List<Any>>

    private val locations = database.getLocations()
    private val events = database.getSchedule()
    private val speakers = database.getSpeakers()

    init {
        results = Transformations.switchMap(query) { text ->
            val results = MediatorLiveData<List<Any>>()

            results.addSource(events) {
                val locations = locations.value ?: emptyList()
                val speakers = speakers.value ?: emptyList()
                setValue(results, text, it, locations, speakers)
            }

            results.addSource(locations) {
                val events = events.value ?: emptyList()
                val speakers = speakers.value ?: emptyList()
                setValue(results, text, events, it, speakers)
            }

            results.addSource(speakers) {
                val events = events.value ?: emptyList()
                val locations = locations.value ?: emptyList()
                setValue(results, text, events, locations, it)
            }

            return@switchMap results
        }
    }

    private fun setValue(results: MediatorLiveData<List<Any>>, query: String, events: List<Event>, locations: List<Location>, speakers: List<Speaker>) {
        if(query.isBlank()) {
            results.value = emptyList()
            return
        }

        val list = ArrayList<Any>()

        val speakers = speakers.filter { it.name.contains(query, true) }
        if (speakers.isNotEmpty()) {
            list.add("Speakers")
            list.addAll(speakers)
        }

        val locations = locations.filter { it.name.contains(query, true) }
        locations.forEach { location ->
            list.add(location)
            // TODO: Should we add the filtered events, or all events for this location?
            list.addAll(events.filter { it.location.name == location.name }.sortedBy { it.start })
        }

        val events = events.filter { it.title.contains(query, true) }
        if(events.isNotEmpty()) {
            list.add("Events")
            list.addAll(events)
        }

        results.value = list
    }

    fun search(text: String?) {
        query.postValue(text)
    }
}