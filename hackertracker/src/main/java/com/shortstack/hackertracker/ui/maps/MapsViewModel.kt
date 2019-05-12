package com.shortstack.hackertracker.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.firebase.FirebaseConferenceMap
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MapsViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val maps: LiveData<List<FirebaseConferenceMap>>
        get() {
            return Transformations.switchMap(database.conference) { id ->
                return@switchMap database.getMaps(id)
            }
        }
}