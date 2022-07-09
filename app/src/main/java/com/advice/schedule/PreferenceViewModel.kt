package com.advice.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.advice.schedule.utilities.Storage
import org.koin.core.KoinComponent
import org.koin.core.inject

class PreferenceViewModel : ViewModel(), KoinComponent {

    private val storage by inject<Storage>()

    private val filterTutorial = MutableLiveData(storage.tutorialFilters)

    fun markFiltersTutorialAsComplete() {
        filterTutorial.value = false
        storage.tutorialFilters = false
    }

    fun getFilterTutorial(): LiveData<Boolean> = filterTutorial

}