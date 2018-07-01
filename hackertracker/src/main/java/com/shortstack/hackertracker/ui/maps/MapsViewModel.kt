package com.shortstack.hackertracker.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.ConferenceMap
import javax.inject.Inject

/**
 * Created by Chris on 6/3/2018.
 */
class MapsViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
    }

    val maps: LiveData<List<ConferenceMap>>
        get() {
            val conference = database.conferenceLiveData
            return Transformations.switchMap(conference) { id ->
                val mutableLiveData = MutableLiveData<List<ConferenceMap>>()

                if (id != null) {
                    mutableLiveData.postValue(listOf(id.conference.maps))
                }

                return@switchMap mutableLiveData
            }
        }
}