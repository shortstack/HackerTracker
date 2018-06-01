package com.shortstack.hackertracker.database

import android.content.Context
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.events.ChangeConEvent
import com.shortstack.hackertracker.events.BusProvider
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.network.FullResponse
import com.shortstack.hackertracker.network.SyncResponse
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Chris on 3/31/2018.
 */
class DatabaseManager(context: Context) {

    private val db: MyRoomDatabase = MyRoomDatabase.buildDatabase(context)

    var con: Conference? = null
        private set

    @Deprecated("Do not use, use the conferences actual title.")
    val databaseName: String = Constants.DEFCON_DATABASE_NAME

    init {

    }

    private fun changeConference(con: Conference) {
        val current = db.conferenceDao().getCurrentCon()

        current.isSelected = false
        con.isSelected = true

        db.conferenceDao().update(listOf(current, con))
    }

    fun getCons(): Single<List<Conference>> {
        return db.conferenceDao().getAll()
    }

//    fun getRecentUpdates(): Flowable<List<Event>> {
//        return db.eventDao().getRecentlyUpdated(db.currentConference?.directory
//                ?: return db.eventDao().getRecentlyUpdated())
//    }

    fun getSchedule(page: Int = 0): Flowable<List<Event>> {
//        return db.eventDao().getFullSchedule(db.currentConference?.directory

//        ?:
        return db.eventDao().getFullSchedule(page)
//        )

    }

    fun getTypes(): Single<List<Type>> {
        return db.typeDao().getTypes(getCurrentCon().directory
                ?: return db.typeDao().getTypes())
    }

    fun changeConference(con: Int) {
        db.conferenceDao().getCon(con).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    changeConference(it)
                    BusProvider.bus.post(ChangeConEvent(getCurrentCon()))
                }, {

                })
    }

    fun getFAQ(): Flowable<List<FAQ>> {
        return db.faqDao().getAll()
    }

    fun getVendors(): Flowable<List<Vendor>> {
        return db.vendorDao().getAll(getCurrentCon().directory
                ?: return db.vendorDao().getAll())
    }

    fun getCurrentCon(): Conference {
        return db.conferenceDao().getCurrentCon()
    }

    fun getEventTypes(): Flowable<List<DatabaseEvent>> {
        return db.eventDao().getEventTypes(getCurrentCon().directory, App.getCurrentDate())
    }

    fun getRecent(): Flowable<List<Event>> {
        return db.eventDao().getRecentlyUpdated(getCurrentCon().directory)
    }

    fun updateConference(body: SyncResponse): Single<Int> {
        db.eventDao().update(body.events)
        return Single.fromCallable { 0 }
    }

    fun updateConference(response: FullResponse): Single<Int> {
        return updateConference(response.syncResponse)
    }

    fun findItem(id: Int): Flowable<Event> {
        return db.eventDao().getEventById(id)
    }

    fun findItem(id: String): Flowable<List<Event>> {
        return db.eventDao().findByText(id)
    }

    fun getTypeForEvent(event: String): Single<Type> {
        return db.typeDao().getTypeForEvent(event)
    }

    fun updateEvent(event: Event) {
        return db.eventDao().update(event)
    }


}