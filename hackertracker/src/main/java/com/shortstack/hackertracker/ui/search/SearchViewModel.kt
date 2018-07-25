package com.shortstack.hackertracker.ui.search

import androidx.lifecycle.*
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

    private val query = MediatorLiveData<String>()

    val results: LiveData<List<DatabaseEvent>>

    init {
        App.application.component.inject(this)

        results = Transformations.switchMap(query) {
            return@switchMap database.findItem("%$it%")
        }
    }

    fun search(text: String) {
        query.postValue(text)
    }
}