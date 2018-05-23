package com.shortstack.hackertracker.di.modules

import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.di.MyApplicationScope
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Chris on 5/21/2018.
 */
@Module
class AnalyticsModule {

    @Provides
    @MyApplicationScope
    @Inject
    fun provideAnalyticsController(storage: SharedPreferencesUtil) = AnalyticsController(storage)
}