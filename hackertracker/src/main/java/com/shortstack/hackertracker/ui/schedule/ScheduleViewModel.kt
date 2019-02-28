package com.shortstack.hackertracker.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FirebaseEvent
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ScheduleViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val result = MediatorLiveData<Resource<List<FirebaseEvent>>>()

    private var source: LiveData<List<FirebaseEvent>>? = null
    val schedule: LiveData<Resource<List<FirebaseEvent>>>
        get() {
            val conference = database.conference
            return Transformations.switchMap(conference) { id ->
                result.value = Resource.loading(null)

                if (id != null) {
                    source?.let {
                        result.removeSource(it)
                    }

                    source = database.getSchedule(id).also {
                        result.addSource(it) {
                            result.value = Resource.success(it)
                        }
                    }

                } else {
                    result.value = Resource.init(null)
                }
                return@switchMap result
            }
        }
}