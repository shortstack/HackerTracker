package com.shortstack.hackertracker.ui.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FAQ
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class InformationViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val result = MediatorLiveData<Resource<List<FAQ>>>()

    val faq: LiveData<Resource<List<FAQ>>>
        get() {
            val conference = database.conference
            return Transformations.switchMap(conference) { id ->
                result.value = Resource.loading(null)

                if (id != null) {
                    result.addSource(database.getFAQ(id)) {
                        result.value = Resource.success(it)
                    }
                } else {
                    result.value = Resource.init(null)
                }
                return@switchMap result
            }
        }
}