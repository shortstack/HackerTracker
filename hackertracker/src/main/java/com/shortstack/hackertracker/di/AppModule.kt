package com.shortstack.hackertracker.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Chris on 5/21/2018.
 */
@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext() = context
}