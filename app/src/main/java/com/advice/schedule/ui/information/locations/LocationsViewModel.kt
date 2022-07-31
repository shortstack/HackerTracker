package com.advice.schedule.ui.information.locations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.schedule.Response
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.local.Location
import com.advice.schedule.views.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
                    val data = list.map { it.toContainer() }
                    // need to populate the list first
                    locations.value = Response.Success(list.map { it.toContainer() })
                    // then update the status
                    locations.value = Response.Success(updateLocations(data))
                }
            }
        }

        viewModelScope.launch {
            while (isActive) {
                delay(30000)
                Log.e("LocationVIewModel", "Updating locations!")
                val list = getCurrentList()
                locations.value = Response.Success(updateLocations(list))
            }
        }
    }

    private fun updateLocations(list: List<LocationContainer>): List<LocationContainer> {
        for (location in list) {

            val children = location.getChildren()
            val status = if (children.isEmpty()) {
                location.getCurrentStatus()
            } else {
                when {
                    children.all { it.status == LocationStatus.Open } -> LocationStatus.Open
                    children.all { it.status == LocationStatus.Closed } -> LocationStatus.Closed
                    else -> LocationStatus.Mixed
                }
            }

            if (location.title == "Caesars Forum") {
                Log.e("", "Found it! $children -- $status")
            }
            location.setStatus(status)
        }
        return list
    }

    private fun getCurrentList(): List<LocationContainer> {
        return (locations.value as? Response.Success<List<LocationContainer>>)?.data ?: emptyList()
    }

    fun toggle(location: LocationContainer) {
        val list = getCurrentList().toMutableList()

        val indexOf = list.indexOf(location)
        val isExpanded = !list[indexOf].isChildrenExpanded
        list[indexOf] = location.isChildrenExpanded(isExpanded)

        val children = location.getChildren()
        for (child in children) {

            list[list.indexOf(child)] = child
                .isExpanded(isExpanded = isExpanded)
                .isChildrenExpanded(isExpanded = isExpanded)
        }

        locations.value = Response.Success(list)
    }

    private fun LocationContainer.getChildren(): List<LocationContainer> {
        val list = getCurrentList()

        var index = list.indexOf(this)
        if (index == -1)
            return emptyList()

        val result = mutableListOf<LocationContainer>()

        while (++index < list.size && list[index].depth > depth) {
            result.add(list[index])
        }
        return result
    }

    fun getLocations(): LiveData<Response<List<LocationContainer>>> = locations
}

fun Location.toContainer(): LocationContainer {
    return LocationContainer(name, hier_depth, schedule ?: emptyList())
}