package com.shortstack.hackertracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class HackerTrackerViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()
    private val storage: Storage by inject()
    private val themes: ThemesManager by inject()


    val speakers: LiveData<Resource<List<Speaker>>>

    init {
        speakers = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Speaker>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.addSource(database.getSpeakers(it)) {
                    result.value = Resource.success(it)
                }
            }


            return@switchMap result
        }

    }


}