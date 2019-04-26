package com.shortstack.hackertracker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.FirebaseLocation
import com.shortstack.hackertracker.models.FirebaseSpeaker
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SearchViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val query = MediatorLiveData<String>()

    val results: LiveData<List<Any>>

    private val locations = ArrayList<FirebaseLocation>()
    private val events = ArrayList<FirebaseEvent>()
    private val speakers = ArrayList<FirebaseSpeaker>()

    init {
        val conference = database.conference.value
                ?: throw IllegalStateException("Current con is null.")

        results = Transformations.switchMap(query) {
            val results = MediatorLiveData<List<Any>>()

            if (it.isBlank()) {
                results.value = emptyList()
            } else {
                val text = "%$it%"

                clear()
                database.search(conference, text)
                        .subscribe { it ->
                            results.postValue(it)
                        }

            }


            return@switchMap results
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