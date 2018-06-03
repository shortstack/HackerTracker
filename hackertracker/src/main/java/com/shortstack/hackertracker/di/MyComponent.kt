package com.shortstack.hackertracker.di

import com.shortstack.hackertracker.database.MyRoomDatabase
import com.shortstack.hackertracker.di.modules.*
import com.shortstack.hackertracker.models.ItemViewModel
import com.shortstack.hackertracker.network.service.UpdateDatabaseService
import com.shortstack.hackertracker.network.task.ReminderJob
import com.shortstack.hackertracker.network.task.SyncJob
import com.shortstack.hackertracker.ui.MainActivityViewModel
import com.shortstack.hackertracker.ui.SearchFragment
import com.shortstack.hackertracker.ui.SettingsFragment
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.activities.SplashActivity
import com.shortstack.hackertracker.ui.home.HomeFragment
import com.shortstack.hackertracker.ui.information.InformationFragment
import com.shortstack.hackertracker.ui.information.InformationViewModel
import com.shortstack.hackertracker.ui.maps.MapsFragment
import com.shortstack.hackertracker.ui.maps.MapsViewModel
import com.shortstack.hackertracker.ui.schedule.ScheduleFragment
import com.shortstack.hackertracker.ui.schedule.ScheduleItemBottomSheet
import com.shortstack.hackertracker.ui.schedule.ScheduleViewModel
import com.shortstack.hackertracker.ui.schedule.list.ScheduleItemAdapter
import com.shortstack.hackertracker.ui.vendors.VendorsFragment
import com.shortstack.hackertracker.ui.vendors.VendorsViewModel
import com.shortstack.hackertracker.utils.NotificationHelper
import com.shortstack.hackertracker.view.FilterView
import com.shortstack.hackertracker.view.ItemView
import dagger.Component

/**
 * Created by Chris on 5/21/2018.
 */
@Component(modules = [(ContextModule::class), (DatabaseModule::class), (SharedPreferencesModule::class),
    (GsonModule::class), (AnalyticsModule::class), (NotificationsModule::class), (DispatcherModule::class)])
@MyApplicationScope
interface MyComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(vendorsFragment: VendorsFragment)

    fun inject(homeFragment: HomeFragment)

    fun inject(informationFragment: InformationFragment)

    fun inject(mapsFragment: MapsFragment)

    fun inject(pagerAdapter: MapsFragment.PagerAdapter)

    fun inject(scheduleItemAdapter: ScheduleItemAdapter)

    fun inject(reminderJob: ReminderJob)

    fun inject(syncJob: SyncJob)

    fun inject(scheduleFragment: ScheduleFragment)

    fun inject(scheduleItemBottomSheet: ScheduleItemBottomSheet)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(splashActivity: SplashActivity)

    fun inject(filterView: FilterView)

    fun inject(itemViewModel: ItemViewModel)

    fun inject(notificationHelper: NotificationHelper)

    fun inject(searchFragment: SearchFragment)

    fun inject(updateDatabaseService: UpdateDatabaseService)

    fun inject(itemView: ItemView)

    fun inject(myRoomDatabase: MyRoomDatabase)

    // ViewModels
    fun inject(mainActivityViewModel: MainActivityViewModel)

    fun inject(vendorsViewModel: VendorsViewModel)

    fun inject(scheduleViewModel: ScheduleViewModel)

    fun inject(informationViewModel: InformationViewModel)

    fun inject(mapsViewModel: MapsViewModel)


}