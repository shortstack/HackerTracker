package com.shortstack.hackertracker.ui.speakers

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Speaker
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SpeakersViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val speakers: LiveData<List<Speaker>>
        get() = database.speakers

}