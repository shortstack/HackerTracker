package com.shortstack.hackertracker.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.models.DatabaseConference
import com.shortstack.hackertracker.models.FirebaseConference
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import javax.inject.Inject

/**
 * Created by Chris on 6/2/2018.
 */
class MainActivityViewModel : ViewModel() {

    @Inject
    lateinit var storage: SharedPreferencesUtil

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
    }

    val conference: LiveData<FirebaseConference>
        get() = database.conferenceLiveData

    val conferences: LiveData<List<Conference>>
        get() = database.getCons()
}