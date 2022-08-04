package com.advice.schedule.ui.information.speakers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.advice.schedule.Response
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.local.Speaker
import org.koin.core.KoinComponent
import org.koin.core.inject

class SpeakersViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val speakers = Transformations.switchMap(database.conference) {
        val result = MediatorLiveData<Response<List<Speaker>>>()

        if (it == null) {
            result.value = Response.Init
        } else {
            result.value = Response.Loading
            result.addSource(database.getSpeakers(it)) {
                result.value = Response.Success(it)
            }
        }

        return@switchMap result
    }

    fun getSpeakers(): LiveData<Response<List<Speaker>>> = speakers

}