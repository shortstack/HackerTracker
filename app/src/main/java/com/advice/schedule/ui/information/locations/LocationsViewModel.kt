package com.advice.schedule.ui.information.locations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.advice.schedule.Response
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.local.Location
import com.advice.schedule.views.LocationContainer
import com.advice.schedule.views.isExpanded
import org.koin.core.KoinComponent
import org.koin.core.inject

class LocationsViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val locations = MediatorLiveData<Response<List<LocationContainer>>>()

    init {
        locations.addSource(database.conference) {
            if (it == null) {
                locations.value = Response.Init
            } else {
                locations.addSource(database.getLocations(it)) {
                    val list = it.sortedWith(compareBy({ it.hier_extent_left }, { it.hier_extent_right }))
                    locations.value = Response.Success(list.map { it.toContainer() })
                }
            }
        }
    }

    fun getLocations(): LiveData<Response<List<LocationContainer>>> = locations

    fun toggle(location: LocationContainer) {
        val list = (locations.value as? Response.Success<List<LocationContainer>>)?.data?.toMutableList() ?: return

        var index = list.indexOf(location) + 1
        while (list[index].depth > location.depth) {
            list[index] = list[index].isExpanded(isExpanded = !list[index].isExpanded)
            index++
        }

        locations.value = Response.Success(list)
    }
}

fun Location.toContainer(): LocationContainer {
    return LocationContainer(name, hier_depth, schedule ?: emptyList())
}