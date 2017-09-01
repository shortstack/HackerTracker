package com.shortstack.hackertracker.vendors

import com.shortstack.hackertracker.di.ActivityScoped
import com.shortstack.hackertracker.di.FragmentScoped
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector;


@Module
public abstract class VendorsModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun vendorsFragment() : VendorsFragment

    @ActivityScoped
    @Binds
    abstract fun vendorsPresenter(presenter : VendorsPresenter) : VendorsContract.Presenter
}