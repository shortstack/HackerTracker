package com.shortstack.hackertracker.ui.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Conference
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class InformationViewModel : ViewModel(), KoinComponent {

    private val database : DatabaseManager by inject()

    val conference: LiveData<Conference>
        get() = database.conference
}