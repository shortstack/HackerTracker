package com.shortstack.hackertracker.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.ConferenceMap
import javax.inject.Inject

/**
 * Created by Chris on 6/3/2018.
 */
class MapsViewModel : ViewModel() {

    companion object {
        private const val DC_26_CAESARS = "dc-26-caesars-public-1.pdf"
        private const val DC_26_FLAMINGO = "dc-26-flamingo-public-1.pdf"
        private const val DC_26_FLAMINGO_NIGHT = "dc-26-flamingo-noct-public.pdf"
        private const val DC_26_LINQ = "dc-26-linq-workshops.pdf"

        private const val TC_20_LEVEL_2 = "tc-20-level-2.pdf"
        private const val TC_20_LEVEL_3 = "tc-20-level-3.pdf"
    }

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
    }

    val maps: LiveData<List<ConferenceMap>>
        get() {
            val conference = database.conferenceLiveData
            return Transformations.switchMap(conference) { id ->
                val mutableLiveData = MutableLiveData<List<ConferenceMap>>()

                if (id != null) {

                    val list = when (id.conference.code) {
                        "DC26" -> listOf(
                                ConferenceMap("Caesars", DC_26_CAESARS),
                                ConferenceMap("Flamingo Day", DC_26_FLAMINGO),
                                ConferenceMap("Flamingo Night", DC_26_FLAMINGO_NIGHT),
                                ConferenceMap("LINQ", DC_26_LINQ))

                        "TC20" -> listOf(
                                ConferenceMap("Level 2", TC_20_LEVEL_2),
                                ConferenceMap("Level 3", TC_20_LEVEL_3))

                        else -> emptyList()
                    }

                    mutableLiveData.postValue(list)
                }

                return@switchMap mutableLiveData
            }
        }
}