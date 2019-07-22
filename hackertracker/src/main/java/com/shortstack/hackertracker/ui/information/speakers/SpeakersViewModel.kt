package com.shortstack.hackertracker.ui.information.speakers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Speaker
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SpeakersViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val speakers: LiveData<List<Speaker>>
        get() = contents

    private val contents = Transformations.switchMap(database.conference) { id ->
        val result = MediatorLiveData<List<Speaker>>()
        if (id == null) {
            return@switchMap result
        }
        return@switchMap database.getSpeakers(id)
    }

}