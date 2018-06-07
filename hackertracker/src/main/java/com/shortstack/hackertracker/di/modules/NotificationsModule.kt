package com.shortstack.hackertracker.di.modules

import android.content.Context
import com.shortstack.hackertracker.di.MyApplicationScope
import com.shortstack.hackertracker.utils.NotificationHelper
import dagger.Module
import dagger.Provides
import javax.inject.Inject

/**
 * Created by Chris on 5/22/2018.
 */
@Module
class NotificationsModule {

    @Provides
    @MyApplicationScope
    @Inject
    fun provideNotificationHelper(context: Context) = NotificationHelper(context)
}