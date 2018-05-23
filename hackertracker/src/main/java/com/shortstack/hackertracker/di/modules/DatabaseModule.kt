package com.shortstack.hackertracker.di.modules

import android.content.Context
import com.shortstack.hackertracker.database.DEFCONDatabaseController
import com.shortstack.hackertracker.di.MyApplicationScope
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import dagger.Module
import dagger.Provides
import javax.inject.Inject

/**
 * Created by Chris on 5/21/2018.
 */
@Module
class DatabaseModule {

    @Provides
    @Inject
    @MyApplicationScope
    fun provideDatabase(context: Context, storage: SharedPreferencesUtil) = DEFCONDatabaseController(context, storage)
}