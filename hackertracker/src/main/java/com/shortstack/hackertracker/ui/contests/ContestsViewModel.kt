package com.shortstack.hackertracker.ui.contests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FirebaseEvent
import javax.inject.Inject

/**
 * Created by Chris on 05/08/18.
 */
class ContestsViewModel : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    private val result = MediatorLiveData<Resource<List<FirebaseEvent>>>()

    init {
        App.application.component.inject(this)
    }

    val contests: LiveData<Resource<List<FirebaseEvent>>>
        get() {
            val conference = database.conference
            return Transformations.switchMap(conference) { id ->
                result.value = Resource.loading(null)

                if (id != null) {
                    result.addSource(database.getContests(id)) {
                        result.value = Resource.success(it)
                    }
                } else {
                    result.value = Resource.init(null)
                }
                return@switchMap result
            }
        }
}