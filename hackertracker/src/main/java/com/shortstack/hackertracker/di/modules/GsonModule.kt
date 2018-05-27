package com.shortstack.hackertracker.di.modules

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.shortstack.hackertracker.di.MyApplicationScope
import dagger.Module
import dagger.Provides

/**
 * Created by Chris on 5/26/2018.
 */
@Module
class GsonModule {

    @Provides
    @MyApplicationScope
    fun provideGson() = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

}