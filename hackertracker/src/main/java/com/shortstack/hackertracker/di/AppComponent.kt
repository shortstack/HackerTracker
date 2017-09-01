package com.shortstack.hackertracker.di

import android.app.Application
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Database.DEFCONDatabaseController
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(ApplicationModule::class, AndroidSupportInjectionModule::class))
public interface AppComponent : AndroidInjector<DaggerApplication> {
    fun inject(application : App)

    //fun getTasksRepository() : TasksRepository

    fun getDatabase() : DEFCONDatabaseController

    override fun inject(instance : DaggerApplication)

    // Gives us syntactic sugar. we can then do DaggerAppComponent.builder().application(this).build().inject(this);
    // never having to instantiate any modules or say which module we are passing the application to.
    // Application will just be provided into our app graph now.
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application : Application) : AppComponent.Builder

        fun build() : AppComponent
    }
}