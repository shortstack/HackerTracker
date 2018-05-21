package com.shortstack.hackertracker.di

import android.content.Context
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import dagger.Module
import dagger.Provides
import javax.inject.Inject

/**
 * Created by Chris on 5/21/2018.
 */
@Module
class SharedPreferencesModule {

    @Provides
    @MyApplicationScope
    @Inject
    fun provideSharedPreferences(context: Context) = SharedPreferencesUtil(context)
}