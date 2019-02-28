package com.shortstack.hackertracker.ui.vendors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Vendor
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class VendorsViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val result = MediatorLiveData<Resource<List<Vendor>>>()

    val vendors: LiveData<Resource<List<Vendor>>>
        get() {
            val conference = database.conference
            return Transformations.switchMap(conference) { id ->
                result.value = Resource.loading(null)

                if (id != null) {
                    result.addSource(database.getVendors(id)) {
                        result.value = Resource.success(it)
                    }
                } else {
                    result.value = Resource.init(null)
                }
                return@switchMap result
            }
        }
}