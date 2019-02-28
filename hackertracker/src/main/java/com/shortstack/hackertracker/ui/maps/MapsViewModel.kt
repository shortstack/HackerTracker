package com.shortstack.hackertracker.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.ConferenceMap
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MapsViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val maps: LiveData<List<ConferenceMap>>
        get() {
            val conference = database.conference
            return Transformations.switchMap(conference) { id ->
                return@switchMap database.getMaps(id)
            }
        }
}