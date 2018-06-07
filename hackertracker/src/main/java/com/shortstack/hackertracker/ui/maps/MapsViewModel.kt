package com.shortstack.hackertracker.ui.maps

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
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
                    mutableLiveData.postValue(listOf(id.maps))
                }

                return@switchMap mutableLiveData
            }
        }
}