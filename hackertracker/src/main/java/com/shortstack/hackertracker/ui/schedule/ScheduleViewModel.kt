package com.shortstack.hackertracker.ui.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Event
import javax.inject.Inject

/**
 * Created by Chris on 6/2/2018.
 */
class ScheduleViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.myComponent.inject(this)
    }

    val schedule: LiveData<List<Event>>
        get() {
            val conference = database.conferenceLiveData
            return Transformations.switchMap(conference) { id ->
                if (id == null) {
                    return@switchMap MutableLiveData<List<Event>>()
                }
                return@switchMap database.getSchedule(id)
            }
        }
}