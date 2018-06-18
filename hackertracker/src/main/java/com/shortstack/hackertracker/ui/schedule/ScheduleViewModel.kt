package com.shortstack.hackertracker.ui.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import javax.inject.Inject

/**
 * Created by Chris on 6/2/2018.
 */
class ScheduleViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    private val result = MediatorLiveData<Resource<List<DatabaseEvent>>>()

    init {
        App.application.component.inject(this)
    }

    val schedule: LiveData<Resource<List<DatabaseEvent>>>
        get() {
            val conference = database.conferenceLiveData
            return Transformations.switchMap(conference) { id ->
                result.value = Resource.loading(null)

                if (id != null) {
                    result.addSource(database.getSchedule(id)) {
                        result.value = Resource.success(it)
                    }
                    result.addSource(database.typesLiveData) {
                        if (it != null)
                            result.addSource(database.getSchedule(id, it)) {
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