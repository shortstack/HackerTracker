package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.room.Transaction
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.network.FullResponse
import com.shortstack.hackertracker.now
import io.reactivex.Single
import java.util.*
import kotlin.math.sign

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

                return@switchMap Transformations.switchMap(getTypes(id.conference)) {
                    val liveData = MediatorLiveData<List<Type>>()


                    val conference = conferenceLiveData.value
                    if (conference?.types != it) {
                        conference?.types = it
                        conferenceLiveData.postValue(conference)
                    }
                    liveData.postValue(it)

                    return@switchMap liveData
                }
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
        return db.eventDao().getRecentlyUpdated(conference.code)
    }

    fun getSchedule(conference: DatabaseConference): LiveData<List<DatabaseEvent>> {
        return getSchedule(conference, conference.types)
    }

    private fun getSchedule(conference: DatabaseConference, list: List<Type>): LiveData<List<DatabaseEvent>> {
        val date = Date().now()

        val selected = list.filter { it.isSelected }.map { it.id }
        if (selected.isEmpty()) return db.eventDao().getSchedule(conference.conference.code, date)
        return db.eventDao().getSchedule(conference.conference.code, date, selected)
    }

    fun getFAQ(conference: Conference): LiveData<List<FAQ>> {
        return db.faqDao().getAll(conference.code)
    }

    fun getVendors(conference: Conference): LiveData<List<Vendor>> {
        return db.vendorDao().getAll(conference.code)
    }

    fun getTypes(conference: Conference): LiveData<List<Type>> {
        return db.typeDao().getTypes(conference.code)
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

    fun updateBookmark(event: Event) {
        return db.eventDao().updateBookmark(event.id, event.isBookmarked)
    }

    fun updateConference(conference: Conference) {
        db.conferenceDao().update(conference)
    }


    @Transaction
    fun updateConference(conference: Conference, body: FullResponse): Int {
        var count = 0

        body.apply {
            types?.let {
                count += db.typeDao().insertAll(it.types).size
            }

            speakers?.let {
                count += db.speakerDao().insertAll(it.speakers).size
            }

            syncResponse?.let {
                count += db.eventDao().insertAll(it.events).size
            }

            vendors?.let {
                count += db.vendorDao().insertAll(it.vendors).size
            }

            faqs?.let {
                count += db.faqDao().insertAll(it.faqs).size
            }

            db.conferenceDao().upsert(conference)
        }

        return count
    }

    fun updateType(type: Type): Int {
        return db.typeDao().update(type)
    }

    fun clear() {
        return db.conferenceDao().deleteAll()
    }

}