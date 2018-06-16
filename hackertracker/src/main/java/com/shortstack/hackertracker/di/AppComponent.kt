package com.shortstack.hackertracker.di

import com.shortstack.hackertracker.database.MyRoomDatabase
import com.shortstack.hackertracker.di.modules.*
import com.shortstack.hackertracker.models.EventViewModel
import com.shortstack.hackertracker.network.task.SyncWorker
import com.shortstack.hackertracker.network.task.ReminderWorker
import com.shortstack.hackertracker.ui.MainActivityViewModel
import com.shortstack.hackertracker.ui.SearchFragment
import com.shortstack.hackertracker.ui.SettingsFragment
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.home.HomeFragment
import com.shortstack.hackertracker.ui.home.HomeViewModel
import com.shortstack.hackertracker.ui.information.InformationViewModel
import com.shortstack.hackertracker.ui.maps.MapsViewModel
import com.shortstack.hackertracker.ui.schedule.ScheduleFragment
import com.shortstack.hackertracker.ui.schedule.EventBottomSheet
import com.shortstack.hackertracker.ui.schedule.ScheduleViewModel
import com.shortstack.hackertracker.ui.schedule.list.ScheduleAdapter
import com.shortstack.hackertracker.ui.search.SearchViewModel
import com.shortstack.hackertracker.ui.vendors.VendorsViewModel
import com.shortstack.hackertracker.utils.NotificationHelper
import com.shortstack.hackertracker.views.FilterView
import com.shortstack.hackertracker.views.EventView
import com.shortstack.hackertracker.views.TimeView
import dagger.Component

/**
 * Created by Chris on 5/21/2018.
 */
@Component(modules = [(ContextModule::class), (DatabaseModule::class), (SharedPreferencesModule::class),
    (GsonModule::class), (AnalyticsModule::class), (NotificationsModule::class), (DispatcherModule::class), (TimerModule::class)])
@MyApplicationScope
interface AppComponent {

    // Components

    fun inject(notificationHelper: NotificationHelper)
    fun inject(myRoomDatabase: MyRoomDatabase)

    // Activities + Fragments

    fun inject(mainActivity: MainActivity)
    fun inject(homeFragment: HomeFragment)
    fun inject(scheduleFragment: ScheduleFragment)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(searchFragment: SearchFragment)

    // Views

    fun inject(scheduleItemAdapter: ScheduleAdapter)
    fun inject(scheduleItemBottomSheet: EventBottomSheet)
    fun inject(filterView: FilterView)
    fun inject(itemView: EventView)
    fun inject(timeView: TimeView)

    // ViewModels

    fun inject(mainActivityViewModel: MainActivityViewModel)
    fun inject(homeViewModel: HomeViewModel)
    fun inject(scheduleViewModel: ScheduleViewModel)
    fun inject(mapsViewModel: MapsViewModel)
    fun inject(informationViewModel: InformationViewModel)
    fun inject(vendorsViewModel: VendorsViewModel)
    fun inject(searchViewModel: SearchViewModel)
    fun inject(itemViewModel: EventViewModel)

    // Background Workers

    fun inject(syncWorker: SyncWorker)
    fun inject(reminderWorker: ReminderWorker)


}