package com.shortstack.hackertracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.FirebaseEvent
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

    val recent: LiveData<List<FirebaseEvent>>
        get() {
            val conference = database.conferenceLiveData
            return Transformations.switchMap(conference) { id ->
                if (id == null) {
                    return@switchMap MutableLiveData<List<FirebaseEvent>>()
                }
                return@switchMap database.getRecent(id)
            }
        }
}