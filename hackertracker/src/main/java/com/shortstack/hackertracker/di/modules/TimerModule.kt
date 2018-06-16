package com.shortstack.hackertracker.di.modules

import com.shortstack.hackertracker.di.MyApplicationScope
import com.shortstack.hackertracker.utils.TickTimer
import dagger.Module
import dagger.Provides

/**
 * Created by Chris on 6/14/2018.
 */
@Module
class TimerModule {

    @Provides
    @MyApplicationScope
    fun provideTimer() = TickTimer()
}