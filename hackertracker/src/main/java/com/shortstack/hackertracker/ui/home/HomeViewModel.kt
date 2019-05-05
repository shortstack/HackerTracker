package com.shortstack.hackertracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.firebase.FirebaseEvent
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class HomeViewModel : ViewModel(), KoinComponent{

    private val database: DatabaseManager by inject()

    val recent: LiveData<List<FirebaseEvent>>
        get() {
            val conference = database.conference
            return Transformations.switchMap(conference) { id ->
                if (id == null) {
                    return@switchMap MutableLiveData<List<FirebaseEvent>>()
                }
                return@switchMap database.getRecent(id)
            }
        }
}