package com.shortstack.hackertracker.di

import androidx.work.WorkManager
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.database.ReminderManager
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.information.speakers.SpeakersViewModel
import com.shortstack.hackertracker.ui.schedule.ScheduleViewModel
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.utilities.NotificationHelper
import com.shortstack.hackertracker.utilities.Storage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { Storage(get(), get()) }
    single { NotificationHelper(get()) }
    single {
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    }
    single { FirebaseJobDispatcher(GooglePlayDriver(get())) }
    single { DatabaseManager(get()) }
    single { ThemesManager(get()) }

    single { Analytics(get(), get()) }
    single { WorkManager.getInstance() }
    single { ReminderManager(get(), get()) }


    viewModel { HackerTrackerViewModel() }
    viewModel { ScheduleViewModel() }
    viewModel { SpeakersViewModel() }

}