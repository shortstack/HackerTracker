package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.toWorkData
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.network.FullResponse
import com.shortstack.hackertracker.network.task.ReminderWorker
import com.shortstack.hackertracker.now
import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Chris on 3/31/2018.
 */
class DatabaseManager(context: Context) {

    companion object {
        private const val TYPE_CONTEST = 7
        private const val TYPE_WORKSHOP = 3
    }


    private val db: HTDatabase

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
        db = HTDatabase.buildDatabase(context, conferenceLiveData)
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
        val date = if(conference.isExpired) {
            conference.conference.start
        } else {
            Date().now()
        }

        var selected = list.filter { !it.isBookmark && it.isSelected }.map { it.id }
        if (selected.isEmpty())
            selected = list.map { it.id }

        selected = selected.filter { it != TYPE_CONTEST && it != TYPE_WORKSHOP }

        val isBookmarked = list.find { it.isBookmark }?.isSelected ?: false
        if (isBookmarked) {
            return db.eventDao().getSchedule(conference.conference.code, date, selected, isBookmarked)
        }

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

    fun getEventById(id: Int): DatabaseEvent? {
        return db.eventDao().getEventById(id)
    }

    fun searchForEvents(conference: Conference, text: String): List<DatabaseEvent> {
        return db.eventDao().getEventByText(conference.code, text)
    }

    fun searchForLocation(conference: Conference, text: String): List<Location> {
        return db.locationDao().getLocationByText(conference.code, text)
    }

    fun searchForSpeaker(conference: Conference, text: String): List<Speaker> {
        return db.speakerDao().findSpeakerByText(conference.code, text)
    }


    fun getTypeForEvent(event: String): Single<Type> {
        return db.typeDao().getTypeForEvent(event)
    }

    fun updateBookmark(event: Event) {
        val tag = "reminder_" + event.id

        if (event.isBookmarked) {
            val delay = event.begin.time - Date().now().time - (1000 * 20 * 60)

            if (delay > 0) {
                val notify = OneTimeWorkRequestBuilder<ReminderWorker>()
                        .setInputData(mapOf(ReminderWorker.NOTIFICATION_ID to event.id).toWorkData())
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .addTag(tag)
                        .build()

                WorkManager.getInstance()?.enqueue(notify)
            }

            AnalyticsController.onEventAction(AnalyticsController.EVENT_BOOKMARK, event)
        } else {
            WorkManager.getInstance()?.cancelAllWorkByTag(tag)
            AnalyticsController.onEventAction(AnalyticsController.EVENT_UNBOOKMARK, event)

        }

        return db.eventDao().updateBookmark(event.id, event.isBookmarked)
    }

    fun updateConference(conference: Conference) {
        db.conferenceDao().update(conference)
    }


    fun updateConference(conference: Conference, body: FullResponse) {
        db.updateDatabase(conference, body)
    }

    fun updateTypeIsSelected(type: Type): Int {
        return db.typeDao().updateSelected(type.id, type.isSelected)
    }

    fun clear() {
        return db.conferenceDao().deleteAll()
    }

    fun getSpeakers(event: Int): List<Speaker> {
        return db.eventSpeakerDao().getSpeakersForEvent(event)
    }

    fun getEventsForSpeaker(speaker: Int): List<DatabaseEvent> {
        return db.eventSpeakerDao().getEventsForSpeaker(speaker)
    }


    fun getUpdatedEventsCount(updatedAt: Date?): Int {
        return db.eventDao().getUpdatedCount(updatedAt)
    }

    fun getUpdatedBookmarks(conference: Conference, updatedAt: Date?): List<DatabaseEvent> {
        if (updatedAt != null)
            return db.eventDao().getUpdatedBookmarks(conference.code, updatedAt)
        return db.eventDao().getUpdatedBookmarks(conference.code)
    }

    fun getRelatedEvents(id: Int, types: List<Type>, speakers: List<Speaker>): List<DatabaseEvent> {
        val result = ArrayList<DatabaseEvent>()

        val speakerEvents = db.eventSpeakerDao().getEventsForSpeakers(speakers.map { it.id })
        result.addAll(speakerEvents)

        // TODO: Improve this, maybe use Date + Type. Same time-block?
        val typeEvents = db.eventDao().getEventByType(types.map { it.id })
        result.addAll(typeEvents.sortedBy { it.location.first().name })

        return result.filter { it.event.id != id }.distinctBy { it.event.id }.take(3)
    }

    fun getContests(conference: Conference): LiveData<List<DatabaseEvent>> {
        return db.eventDao().getContests(conference.code, Date().now())
    }

    fun getWorkshops(conference: Conference): LiveData<List<DatabaseEvent>> {
        return db.eventDao().getWorkshops(conference.code, Date().now())
    }

}