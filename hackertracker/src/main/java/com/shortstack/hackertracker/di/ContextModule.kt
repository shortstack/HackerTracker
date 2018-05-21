package com.shortstack.hackertracker.di

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * Created by Chris on 5/21/2018.
 */
@Module
class ContextModule(private val context: Context) {

    @Provides
    @MyApplicationScope
    fun provideContext() = context

}