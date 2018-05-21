package com.shortstack.hackertracker.di

import android.content.Context
import com.shortstack.hackertracker.database.DEFCONDatabaseController
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
    fun provideDatabase(context: Context) = DEFCONDatabaseController(context)
}