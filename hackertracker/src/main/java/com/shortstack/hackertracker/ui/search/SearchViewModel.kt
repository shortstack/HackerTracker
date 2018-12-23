package com.shortstack.hackertracker.ui.search

import androidx.lifecycle.*
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.*
import java.lang.IllegalStateException
import javax.inject.Inject

/**
 * Created by Chris on 6/3/2018.
 */
class SearchViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    private val query = MediatorLiveData<String>()

    val results: LiveData<List<Any>>

    private val locations = ArrayList<FirebaseLocation>()
    private val events = ArrayList<FirebaseEvent>()
    private val speakers = ArrayList<FirebaseSpeaker>()

    init {
        App.application.component.inject(this)

        val conference = database.conference.value ?: throw IllegalStateException("Current con is null.")

        results = Transformations.switchMap(query) {
            val result = MediatorLiveData<List<Any>>()

            if (it.isBlank()) {
                result.value = emptyList()
            } else {
                val text = "%$it%"

                clear()
                speakers.addAll(database.searchForSpeaker(conference, text))
                events.addAll(database.searchForEvents(conference, text))
                locations.addAll(database.searchForLocation(conference, text))

                setValue(result)
            }


            return@switchMap result

        }
    }

    private fun clear() {
        speakers.clear()
        events.clear()
        locations.clear()
    }

    private fun setValue(result: MediatorLiveData<List<Any>>) {
        val temp = ArrayList<Any>()

        val tempEvents = ArrayList<FirebaseEvent>()
        tempEvents.addAll(events)

        if (speakers.isNotEmpty()) {
            temp.add("Speakers")
            temp.addAll(speakers)
        }

        if (locations.isNotEmpty()) {
            locations.forEach { loc ->
                temp.add(loc)

                val events = tempEvents.filter { it.location.name == loc.name }.sortedBy { it.begin }
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