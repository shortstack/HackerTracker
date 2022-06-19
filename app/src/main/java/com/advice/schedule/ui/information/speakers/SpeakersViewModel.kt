package com.shortstack.hackertracker.ui.information.speakers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.Response
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Speaker
import org.koin.core.KoinComponent
import org.koin.core.inject

class SpeakersViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val speakers = Transformations.switchMap(database.conference) {
        val result = MediatorLiveData<Response<List<Speaker>>>()

        if (it == null) {
            result.value = Response.Init
        } else {
            result.addSource(database.getSpeakers(it)) {
                result.value = Response.Success(it)
            }
        }

        return@switchMap result
    }

    fun getSpeakers(): LiveData<Response<List<Speaker>>> = speakers

}