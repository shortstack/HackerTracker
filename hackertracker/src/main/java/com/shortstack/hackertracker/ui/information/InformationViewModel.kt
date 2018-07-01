package com.shortstack.hackertracker.ui.information

import androidx.lifecycle.*
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.FAQ
import com.shortstack.hackertracker.models.Vendor
import javax.inject.Inject

/**
 * Created by Chris on 6/3/2018.
 */
class InformationViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    private val result = MediatorLiveData<Resource<List<FAQ>>>()

    init {
        App.application.component.inject(this)
    }

    val faq: LiveData<Resource<List<FAQ>>>
        get() {
            val conference = database.conferenceLiveData
            return Transformations.switchMap(conference) { id ->
                result.value = Resource.loading(null)

                if (id != null) {
                    result.addSource(database.getFAQ(id.conference)) {
                        result.value = Resource.success(it)
                    }
                } else {
                    result.value = Resource.init(null)
                }
                return@switchMap result
            }
        }
}