package com.shortstack.hackertracker.ui.search

import androidx.lifecycle.*
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Location
import com.shortstack.hackertracker.models.Speaker
import javax.inject.Inject

/**
 * Created by Chris on 6/3/2018.
 */
class SearchViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    private val query = MediatorLiveData<String>()

    val results: LiveData<List<Any>>

    private val locations = ArrayList<Location>()
    private val events = ArrayList<DatabaseEvent>()
    private val speakers = ArrayList<Speaker>()

    init {
        App.application.component.inject(this)

        results = Transformations.switchMap(query) {

            if (it.isBlank()) {
                val data = MediatorLiveData<List<Any>>()
                data.value = emptyList()
                return@switchMap data
            }


            val text = "%$it%"

            val result = MediatorLiveData<List<Any>>()
            result.addSource(database.searchForSpeaker(text)) {
                speakers.clear()
                speakers.addAll(it)

                setValue(result)
            }

            result.addSource(database.search(text)) {
                events.clear()
                events.addAll(it)

                setValue(result)
            }

            result.addSource(database.searchForLocation(text)) {
                locations.clear()
                locations.addAll(it)

                setValue(result)
            }

            return@switchMap result
        }
    }

    private fun setValue(result: MediatorLiveData<List<Any>>) {
        val temp = ArrayList<Any>()

        val tempEvents = ArrayList<DatabaseEvent>()
        tempEvents.addAll(events)

        if (speakers.isNotEmpty()) {
            temp.add("Speakers")
            temp.addAll(speakers)
        }

        if (locations.isNotEmpty()) {
            locations.forEach { loc ->
                temp.add(loc)

                val events = tempEvents.filter { it.event.location == loc.id }
                tempEvents.removeAll(events)

                temp.addAll(events)
            }
        }

        if (tempEvents.isNotEmpty()) {
            temp.add("Events")
            temp.addAll(tempEvents)
        }

        result.value = temp
    }


    fun search(text: String?) {
        query.postValue(text)
    }
}