package com.shortstack.hackertracker.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.firebase.FirebaseHacker
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SettingsViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val hacker: LiveData<FirebaseHacker>
        get() = database.getUser()

}