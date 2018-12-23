package com.shortstack.hackertracker.di.modules

import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.di.MyApplicationScope
import dagger.Module
import dagger.Provides

/**
 * Created by Chris on 5/21/2018.
 */
@Module
class DatabaseModule {

    @Provides
    @MyApplicationScope
    fun provideDatabase() = DatabaseManager()
}