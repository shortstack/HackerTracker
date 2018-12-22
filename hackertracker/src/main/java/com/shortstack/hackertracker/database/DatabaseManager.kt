package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.toWorkData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.network.FullResponse
import com.shortstack.hackertracker.network.task.ReminderWorker
import com.shortstack.hackertracker.now
import io.reactivex.Completable
import io.reactivex.Single
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.NullPointerException
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

    // TODO: Remove.
    private val db: HTDatabase = HTDatabase.buildDatabase(context)


    val database = FirebaseDatabase.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val conferenceLiveData = MutableLiveData<FirebaseConference>()

    val typesLiveData = MutableLiveData<List<FirebaseType>>()

    init {

//        database.setPersistenceEnabled(true)
        val database = database.reference

        firestore.collection("conferences")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result?.documents?.forEach {

                            val con = it.toObject(FirebaseConference::class.java)

                            conferenceLiveData.postValue(con)


                        }
                    }
                }


        database.child("conferences").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Logger.e("Cancelled!")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Logger.d("Data has changed!")
                val cons = snapshot.children.map {
                    it.getValue(FirebaseConference::class.java)
                }
                val defcon = cons.first()!!





                conferenceLiveData.postValue(defcon)

//                typesLiveData.postValue(defcon.types.values.toList())
            }
        })
    }

    private fun getCurrentCon(): DatabaseConference? {
        return db.conferenceDao().getCurrentCon()
    }

    fun changeConference(con: DatabaseConference) {

    }

    fun getCons(): LiveData<List<Conference>> {
        return db.conferenceDao().getAll()
    }

    fun getConferences(): List<DatabaseConference> {
        return db.conferenceDao().get()
    }

    fun getRecent(conference: FirebaseConference): LiveData<List<FirebaseEvent>> {
        val mutableLiveData = MutableLiveData<List<FirebaseEvent>>()

        FirebaseDatabase.getInstance().getReference("conferences/DC26/events").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val events = p0.children.map {
                    it.getValue(FirebaseEvent::class.java) ?: return
                }.sortedBy { it.begin }.take(20)

                mutableLiveData.postValue(events)
            }

        })



        return mutableLiveData
    }

    fun getSchedule(conference: FirebaseConference): LiveData<List<FirebaseEvent>> {
        return getSchedule(conference, emptyList())
    }

    private fun getSchedule(conference: FirebaseConference, list: List<Type>): LiveData<List<FirebaseEvent>> {
        val date = Date().now()


        val mutableLiveData = MutableLiveData<List<FirebaseEvent>>()

        firestore.collection("conferences")
                .document("TC20")
                .collection("events")
                .get()
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val events = task.result?.documents?.mapNotNull { it.toObject(FirebaseEvent::class.java) }

                        if (events != null)
                            mutableLiveData.postValue(events)
                    }
                }



        return mutableLiveData
    }

    fun getFAQ(conference: FirebaseConference): LiveData<List<FAQ>> {
        return db.faqDao().getAll(conference.code)
    }

    fun getVendors(conference: FirebaseConference): LiveData<List<Vendor>> {
        return db.vendorDao().getAll(conference.code)
    }

    fun getTypes(conference: FirebaseConference): LiveData<List<Type>> {
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


    fun getTypeForEvent(event: FirebaseEvent): Single<FirebaseType> {

        val id = event.type

        return Single.create<FirebaseType> { emitter ->

            if (id == null) {
                emitter.onError(IllegalArgumentException("Event cannot have no types."))
            }

            emitter.onSuccess(id)
        }
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

    fun getSpeakers(event: FirebaseEvent): Single<List<FirebaseSpeaker>> {
        return Single.create<List<FirebaseSpeaker>> { emitter ->

            val id = event.speakers

            if (id == null) {
                emitter.onSuccess(emptyList())
                return@create
            }

            emitter.onSuccess(id)
        }
    }

    fun getEventsForSpeaker(speaker: FirebaseSpeaker): Single<List<FirebaseEvent>> {
        return Single.create<List<FirebaseEvent>> { emitter ->

            val id = speaker.events.keys.firstOrNull()

            if (id == null) {
                emitter.onSuccess(emptyList())
                return@create
            }

            FirebaseDatabase.getInstance().getReference("conferences/DC26/speakers/$id").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    emitter.onError(p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val value = p0.getValue(FirebaseEvent::class.java)
                    if (value != null) {
                        emitter.onSuccess(listOf(value))
                    } else {
                        emitter.onSuccess(emptyList())
                    }
                }

            })

        }
    }


    fun getUpdatedEventsCount(updatedAt: Date?): Int {
        return db.eventDao().getUpdatedCount(updatedAt)
    }

    fun getUpdatedBookmarks(conference: FirebaseConference, updatedAt: Date?): List<DatabaseEvent> {
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
        result.addAll(typeEvents.sortedBy { it.location.firstOrNull()?.name })

        return result.filter { it.event.id != id }.distinctBy { it.event.id }.take(3)
    }

    fun getContests(conference: FirebaseConference): LiveData<List<DatabaseEvent>> {
        return db.eventDao().getContests(conference.code, Date().now())
    }

    fun getWorkshops(conference: FirebaseConference): LiveData<List<DatabaseEvent>> {
        return db.eventDao().getWorkshops(conference.code, Date().now())
    }

}