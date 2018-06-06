package com.shortstack.hackertracker.database

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.network.FullResponse
import com.shortstack.hackertracker.network.SyncResponse
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Chris on 3/31/2018.
 */
class DatabaseManager(context: Context) {

    private val db: MyRoomDatabase

    val conferenceLiveData = MutableLiveData<Conference>()

    val typesLiveData: LiveData<List<Type>>
        get() {
            return Transformations.switchMap(conferenceLiveData) { id ->
                if (id == null) {
                    return@switchMap MutableLiveData<List<Type>>()
                }
                return@switchMap getTypes(id)
            }
        }

    init {
        db = MyRoomDatabase.buildDatabase(context, conferenceLiveData)
        val currentCon = getCurrentCon()
        conferenceLiveData.postValue(currentCon)
    }

    private fun getCurrentCon(): Conference {
        return db.conferenceDao().getCurrentCon()
    }

    fun changeConference(con: Conference) {
        conferenceLiveData.postValue(con)

        val current = db.conferenceDao().getCurrentCon()

        current.isSelected = false
        con.isSelected = true

        db.conferenceDao().update(listOf(current, con))
    }

    fun getCons(): LiveData<List<Conference>> {
        return db.conferenceDao().getAll()
    }

    fun getConferences(): List<Conference> {
        return db.conferenceDao().get()
    }

    fun getRecent(conference: Conference): LiveData<List<Event>> {
        return db.eventDao().getRecentlyUpdated(conference.directory)
    }

    // TODO: Implement paging.
    fun getSchedule(conference: Conference, page: Int = 0): LiveData<List<Event>> {
        return db.eventDao().getSchedule(conference.directory)
    }

    fun getFAQ(conference: Conference): LiveData<List<FAQ>> {
        return db.faqDao().getAll(conference.directory)
    }

    fun getVendors(conference: Conference): LiveData<List<Vendor>> {
        return db.vendorDao().getAll(conference.directory)
    }

    fun getTypes(conference: Conference): LiveData<List<Type>> {
        return db.typeDao().getTypes(conference.directory)
    }

    fun findItem(id: Int): Event? {
        return db.eventDao().getEventById(id)
    }

    fun findItem(id: String): LiveData<List<Event>> {
        return db.eventDao().findByText(id)
    }

    fun getTypeForEvent(event: String): Single<Type> {
        return db.typeDao().getTypeForEvent(event)
    }

    fun updateEvent(event: Event) {
        return db.eventDao().update(event)
    }

    fun updateConference(conference: Conference) {
        db.conferenceDao().update(conference)
    }


    fun updateConference(conference: Conference, body: SyncResponse): Int {
        body.events.forEach {
            it.con = conference.directory
        }
        db.conferenceDao().insert(conference)
        db.eventDao().insertAll(body.events)
        return body.events.size
    }

//    fun updateConference(conference: Conference, response: FullResponse): Single<Int> {
//        return updateConference(conference, response.syncResponse)
//    }

    fun updateType(type: Type): Int {
        return db.typeDao().update(type)
    }

}