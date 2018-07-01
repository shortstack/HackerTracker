package com.shortstack.hackertracker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import javax.inject.Inject

/**
 * Created by Chris on 6/3/2018.
 */
class SearchViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
    }

    fun getResult(text: String): LiveData<List<DatabaseEvent>> {
        val conference = database.conferenceLiveData
        return Transformations.switchMap(conference) { id ->
            if (id == null) {
                return@switchMap MutableLiveData<List<DatabaseEvent>>()
            }
            return@switchMap database.findItem("%$text%")
        }
    }
}