package com.shortstack.hackertracker.database

import android.content.Context
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Event.ChangeConEvent
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.Type
import com.shortstack.hackertracker.models.Vendor
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Chris on 3/31/2018.
 */
class DatabaseManager(context: Context) {

    val db: MyRoomDatabase = MyRoomDatabase.buildDatabase(context)

    fun changeConference(con: Conference) {
        val current = db.currentConference

        if (current != null) {
            current.isSelected = false
            Logger.d("Updating current: " + current.index + " " + current.isSelected)
            db.conferenceDao().update(current)
        }
        con.isSelected = true
        Logger.d("Updating con: " + con.index + " " + con.isSelected)
        db.conferenceDao().update(con)

        db.currentConference = con
    }

    fun getCons(): Single<List<Conference>> {
        return db.conferenceDao().getAll()
    }

    fun getRecentUpdates(): Flowable<List<Event>> {
        return db.eventDao().getRecentlyUpdated(db.currentConference?.directory
                ?: return db.eventDao().getRecentlyUpdated())
    }

    fun getSchedule(): Flowable<List<Event>> {
        return db.eventDao().getFullSchedule(db.currentConference?.directory
                ?: return db.eventDao().getFullSchedule())

    }

    fun getTypes(): Single<List<Type>> {
        return db.typeDao().getTypes(db.currentConference?.directory
                ?: return db.typeDao().getTypes())
    }

    fun changeConference(con: Int) {
        db.conferenceDao().getCon(con).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    changeConference(it)
                    App.application.postBusEvent(ChangeConEvent())
                }, {

                })
    }

    fun getVendors(): Flowable<List<Vendor>> {
        return db.vendorDao().getAll(db.currentConference?.directory
                ?: return db.vendorDao().getAll())
    }
}