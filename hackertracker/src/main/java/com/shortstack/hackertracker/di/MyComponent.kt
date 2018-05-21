package com.shortstack.hackertracker.di

import com.shortstack.hackertracker.di.modules.ContextModule
import com.shortstack.hackertracker.di.modules.DatabaseModule
import com.shortstack.hackertracker.di.modules.SharedPreferencesModule
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.home.HomeFragment
import com.shortstack.hackertracker.ui.information.InformationFragment
import com.shortstack.hackertracker.ui.vendors.VendorsFragment
import dagger.Component

/**
 * Created by Chris on 5/21/2018.
 */
@Component(modules = arrayOf(ContextModule::class, DatabaseModule::class, SharedPreferencesModule::class))
@MyApplicationScope
interface MyComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(vendorsFragment: VendorsFragment)

    fun inject(homeFragment: HomeFragment)

    fun inject(informationFragment: InformationFragment)

}