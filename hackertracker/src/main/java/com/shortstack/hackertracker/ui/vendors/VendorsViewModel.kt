package com.shortstack.hackertracker.ui.vendors

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Vendor
import javax.inject.Inject

/**
 * Created by Chris on 6/2/2018.
 */
class VendorsViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
    }

    val vendors: LiveData<List<Vendor>>
        get() {
            val conference = database.conferenceLiveData
            return Transformations.switchMap(conference) { id ->
                database.getVendors(id)
            }
        }
}