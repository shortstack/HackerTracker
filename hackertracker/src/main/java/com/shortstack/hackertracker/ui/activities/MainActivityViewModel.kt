package com.shortstack.hackertracker.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.models.local.Conference
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MainActivityViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val conference: LiveData<Conference>
        get() = database.conference

    val conferences: LiveData<List<Conference>>
        get() = database.getConferences()

    val types: LiveData<List<Type>>
        get() = database.types

    fun changeConference(itemId: Int) {
        database.changeConference(itemId)
    }
}