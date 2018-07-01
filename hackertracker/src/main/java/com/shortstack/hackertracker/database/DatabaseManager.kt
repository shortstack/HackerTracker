package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import android.content.Context
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.network.FullResponse
import io.reactivex.Single

/**
 * Created by Chris on 3/31/2018.
 */
class DatabaseManager(context: Context) {

    private val db: MyRoomDatabase

    val conferenceLiveData = MutableLiveData<DatabaseConference>()

    val typesLiveData: LiveData<List<Type>>
        get() {
            return Transformations.switchMap(conferenceLiveData) { id ->
                if (id == null) {
                    return@switchMap MutableLiveData<List<Type>>()
                }
                return@switchMap getTypes(id.conference)
            }
        }

    init {
        db = MyRoomDatabase.buildDatabase(context, conferenceLiveData)
        val currentCon = getCurrentCon()
        conferenceLiveData.postValue(currentCon)
    }

    private fun getCurrentCon(): DatabaseConference? {
        return db.conferenceDao().getCurrentCon()
    }

    fun changeConference(con: DatabaseConference) {
        if (con == conferenceLiveData.value) return

        con.conference.isSelected = true

        conferenceLiveData.postValue(con)

        val current = db.conferenceDao().getCurrentCon()
        if (current != null) {
            current.conference.isSelected = false
            db.conferenceDao().update(listOf(current.conference, con.conference))
        } else {
            db.conferenceDao().update(con.conference)
        }
    }

    fun getCons(): LiveData<List<Conference>> {
        return db.conferenceDao().getAll()
    }

    fun getConferences(): List<DatabaseConference> {
        return db.conferenceDao().get()
    }

    fun getRecent(conference: Conference): LiveData<List<DatabaseEvent>> {
        return db.eventDao().getRecentlyUpdated(conference.directory)
    }

    fun getSchedule(conference: DatabaseConference): LiveData<List<DatabaseEvent>> {
        return getSchedule(conference, conference.types)
    }

    fun getSchedule(conference: DatabaseConference, list: List<Type>): LiveData<List<DatabaseEvent>> {
        val selected = list.filter { it.isSelected }.map { it.type }
        if (selected.isEmpty()) return db.eventDao().getSchedule(conference.conference.directory)
        return db.eventDao().getSchedule(conference.conference.directory, selected)
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

    fun findItem(id: String): LiveData<List<DatabaseEvent>> {
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


    fun updateConference(conference: Conference, body: FullResponse): Int {
        body.apply {
            types?.let {
                it.types.forEach {
                    it.con = conference.directory
                }

                db.typeDao().insertAll(it.types)
            }

            speakers?.let {
                it.speakers.forEach {
                    it.con = conference.directory
                }

                db.speakerDao().insertAll(it.speakers)
            }

            syncResponse?.let {
                it.events.forEach {
                    it.con = conference.directory
                }

                db.eventDao().insertAll(it.events)
            }

            vendors?.let {
                it.vendors.forEach {
                    it.con = conference.directory
                }

                db.vendorDao().insertAll(it.vendors)
            }

            faqs?.let {
                it.faqs.forEach {
                    it.con = conference.directory
                }

                db.faqDao().insertAll(it.faqs)
            }

            db.conferenceDao().upsert(conference)
        }

        return body.syncResponse?.events?.size ?: 0
    }

    fun updateType(type: Type): Int {
        return db.typeDao().update(type)
    }

    fun clear() {
        return db.conferenceDao().deleteAll()
    }

}