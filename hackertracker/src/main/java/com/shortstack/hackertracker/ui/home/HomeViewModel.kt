package com.shortstack.hackertracker.ui.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import javax.inject.Inject

/**
 * Created by Chris on 6/3/2018.
 */
class HomeViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
    }

    val recent: LiveData<List<DatabaseEvent>>
        get() {
            val conference = database.conferenceLiveData
            return Transformations.switchMap(conference) { id ->
                if (id == null) {
                    return@switchMap MutableLiveData<List<DatabaseEvent>>()
                }
                return@switchMap database.getRecent(id.conference)
            }
        }
}