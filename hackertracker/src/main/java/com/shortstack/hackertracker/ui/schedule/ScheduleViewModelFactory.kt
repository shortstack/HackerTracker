package com.shortstack.hackertracker.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shortstack.hackertracker.models.local.Type

class ScheduleViewModelFactory(private val type: Type? = null) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ScheduleViewModel(type) as T
    }
}