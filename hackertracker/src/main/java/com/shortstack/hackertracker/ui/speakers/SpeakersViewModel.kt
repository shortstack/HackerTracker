package com.shortstack.hackertracker.ui.speakers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.FirebaseSpeaker
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SpeakersViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val speakers: LiveData<List<FirebaseSpeaker>>
        get() = contents

    private val contents = Transformations.switchMap(database.conference) { id ->
        val result = MediatorLiveData<List<FirebaseSpeaker>>()

        if (id == null) {
            return@switchMap result
        }
        return@switchMap database.getSpeakers(id)
    }

}