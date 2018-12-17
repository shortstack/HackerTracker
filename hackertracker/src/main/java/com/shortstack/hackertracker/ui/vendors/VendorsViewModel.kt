package com.shortstack.hackertracker.ui.vendors

import androidx.lifecycle.*
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Vendor
import javax.inject.Inject

/**
 * Created by Chris on 6/2/2018.
 */
class VendorsViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    private val result = MediatorLiveData<Resource<List<Vendor>>>()

    init {
        App.application.component.inject(this)
    }

    val vendors: LiveData<Resource<List<Vendor>>>
        get() {
            val conference = database.conferenceLiveData
            return Transformations.switchMap(conference) { id ->
                result.value = Resource.loading(null)

                if (id != null) {
                    result.addSource(database.getVendors(id), {
                        result.value = Resource.success(it)
                    })
                } else {
                    result.value = Resource.init(null)
                }
                return@switchMap result
            }
        }
}