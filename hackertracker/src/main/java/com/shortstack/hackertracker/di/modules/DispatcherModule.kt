package com.shortstack.hackertracker.di.modules

import android.content.Context
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.shortstack.hackertracker.di.MyApplicationScope
import dagger.Module
import dagger.Provides
import javax.inject.Inject

/**
 * Created by Chris on 5/22/2018.
 */
@Module
class DispatcherModule {

    @Provides
    @Inject
    @MyApplicationScope
    fun provideDispatcher(context: Context) = FirebaseJobDispatcher(GooglePlayDriver(context))
}