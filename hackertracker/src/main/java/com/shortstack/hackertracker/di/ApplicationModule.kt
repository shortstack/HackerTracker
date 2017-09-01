package com.shortstack.hackertracker.di

import android.app.Application
import android.content.Context
import com.shortstack.hackertracker.Database.DEFCONDatabaseController
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil
import dagger.Binds
import dagger.Module
import dagger.Provides



@Module
abstract class ApplicationModule(private val app : Application) {

    @Binds
    abstract fun bindContext(application : Application)

    @Provides
    fun provideContext() : Context {
        return app
    }

    @Provides
    fun provideSharedPrefs() : SharedPreferencesUtil {
        return SharedPreferencesUtil()
    }

    @Provides
    fun provideDatabase() : DEFCONDatabaseController {
        return DEFCONDatabaseController(app)
    }
}