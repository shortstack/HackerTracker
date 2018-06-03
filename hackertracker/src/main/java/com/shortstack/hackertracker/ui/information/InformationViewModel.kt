package com.shortstack.hackertracker.ui.information

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FAQ
import javax.inject.Inject

/**
 * Created by Chris on 6/3/2018.
 */
class InformationViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.myComponent.inject(this)
    }

    val faq: LiveData<List<FAQ>>
        get() {
            val conference = database.conferenceLiveData
            return Transformations.switchMap(conference) { id ->
                database.getFAQ(id)
            }
        }
}