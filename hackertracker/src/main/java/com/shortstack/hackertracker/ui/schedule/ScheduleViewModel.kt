package com.shortstack.hackertracker.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FirebaseEvent
import javax.inject.Inject

/**
 * Created by Chris on 6/2/2018.
 */
class ScheduleViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    private val result = MediatorLiveData<Resource<List<FirebaseEvent>>>()

    init {
        App.application.component.inject(this)
    }

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