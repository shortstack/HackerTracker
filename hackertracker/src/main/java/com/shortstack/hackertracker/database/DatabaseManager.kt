package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.toWorkData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.network.task.ReminderWorker
import com.shortstack.hackertracker.now
import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Chris on 3/31/2018.
 */
class DatabaseManager {

    companion object {
        private const val CONFERENCES = "conferences"

        private const val EVENTS = "events"
        private const val TYPES = "types"
        private const val FAQS = "faqs"
        private const val VENDORS = "vendors"
    }

    private val firestore = FirebaseFirestore.getInstance()

    val conference = MutableLiveData<FirebaseConference>()
    val types = MutableLiveData<List<FirebaseType>>()

    init {
        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()

        firestore.firestoreSettings = settings


        firestore.collection(CONFERENCES)
                .get()
                .addOnSuccessListener {

                    val cons = it.toObjects(FirebaseConference::class.java)
                    val con = cons.firstOrNull()

                    conference.postValue(con)

                    if (con != null) {
                        firestore.collection(CONFERENCES)
                                .document(con.code)
                                .collection(TYPES)
                                .get().addOnSuccessListener {
                                    val types = it.toObjects(FirebaseType::class.java)
                                    this.types.postValue(types)
                                }
                    }
                }
    }

    fun changeConference(id: Int) {
        firestore.collection(CONFERENCES)
                .document(id.toString())
                .get()
                .addOnSuccessListener {
                    val con = it.toObject(FirebaseConference::class.java)

                    // TODO: Handle setting the conference as the currently selected con.

                    conference.postValue(con)
                }
    }

    fun getConferences(): LiveData<List<FirebaseConference>> {
        val mutableLiveData = MutableLiveData<List<FirebaseConference>>()

        firestore.collection(CONFERENCES)
                .get()
                .addOnSuccessListener {
                    val cons = it.toObjects(FirebaseConference::class.java)
                    mutableLiveData.postValue(cons)
                }

        return mutableLiveData
    }

    fun getRecent(conference: FirebaseConference): LiveData<List<FirebaseEvent>> {
        val mutableLiveData = MutableLiveData<List<FirebaseEvent>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .get()
                .addOnSuccessListener {
                    val events = it.toObjects(FirebaseEvent::class.java)
                    val recent = events.sortedBy { it.updated }.take(10)
                    mutableLiveData.postValue(recent)
                }

        return mutableLiveData
    }

    fun getSchedule(conference: FirebaseConference): LiveData<List<FirebaseEvent>> {
        return getSchedule(conference, emptyList())
    }

    private fun getSchedule(conference: FirebaseConference, list: List<FirebaseType>): LiveData<List<FirebaseEvent>> {

        // TODO Handle the date of the confrence.
        val date = Date().now()

        val mutableLiveData = MutableLiveData<List<FirebaseEvent>>()


        // TODO: Handle searching by selected types.
        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .get()
                .addOnSuccessListener {
                    val events = it.toObjects(FirebaseEvent::class.java)
                    mutableLiveData.postValue(events)
                }

        return mutableLiveData
    }

    fun getFAQ(conference: FirebaseConference): LiveData<List<FAQ>> {
        val mutableLiveData = MutableLiveData<List<FAQ>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(FAQS)
                .get()
                .addOnSuccessListener {
                    val faqs = it.toObjects(FAQ::class.java)
                    mutableLiveData.postValue(faqs)
                }

        return mutableLiveData
    }

    fun getVendors(conference: FirebaseConference): LiveData<List<Vendor>> {
        val mutableLiveData = MutableLiveData<List<Vendor>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(VENDORS)
                .get()
                .addOnSuccessListener {
                    val vendors = it.toObjects(Vendor::class.java)
                    mutableLiveData.postValue(vendors)
                }
        return mutableLiveData
    }

    fun getEventById(id: Int): MutableLiveData<FirebaseEvent> {
        val mutableLiveData = MutableLiveData<FirebaseEvent>()

        firestore.collection(CONFERENCES)
                .document(id.toString())
                .get()
                .addOnSuccessListener {
                    val event = it.toObject(FirebaseEvent::class.java)
                    mutableLiveData.postValue(event)
                }

        return mutableLiveData
    }

    fun searchForEvents(conference: FirebaseConference, text: String): List<FirebaseEvent> {
        TODO("Need to implement a single threaded solution for searching.")
    }

    fun searchForLocation(conference: FirebaseConference, text: String): List<FirebaseLocation> {
        TODO("Need to implement a single threaded solution for searching.")
    }

    fun searchForSpeaker(conference: FirebaseConference, text: String): List<FirebaseSpeaker> {
        TODO("Need to implement a single threaded solution for searching.")
    }


    fun getTypeForEvent(event: FirebaseEvent): Single<FirebaseType> {
        return Single.create<FirebaseType> { emitter ->
            emitter.onSuccess(event.type)
        }
    }

    fun updateBookmark(event: FirebaseEvent) {
        val tag = "reminder_" + event.id

        if (event.isBookmarked) {
            val delay = event.start.time - Date().now().time - (1000 * 20 * 60)

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

        // TODO: Update the bookmark within Firestore.
    }

    fun updateTypeIsSelected(type: FirebaseType) {
        // TODO: Update the type within Firestore.

    }

    fun clear() {

    }

    fun getSpeakers(event: FirebaseEvent): Single<List<FirebaseSpeaker>> {
        return Single.create<List<FirebaseSpeaker>> { emitter ->
            emitter.onSuccess(event.speakers)
        }
    }

    fun getEventsForSpeaker(speaker: FirebaseSpeaker): Single<List<FirebaseEvent>> {
        return Single.create<List<FirebaseEvent>> { emitter ->
            emitter.onSuccess(speaker.events)
        }
    }


    fun getContests(conference: FirebaseConference): MutableLiveData<List<FirebaseEvent>> {
        val mutableLiveData = MutableLiveData<List<FirebaseEvent>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .get()
                .addOnSuccessListener {
                    val events = it.toObjects(FirebaseEvent::class.java)

                    val contents = events.filter { it.type.name == "Contest" }
                    mutableLiveData.postValue(contents)
                }

        return mutableLiveData
    }

    fun getWorkshops(conference: FirebaseConference): MutableLiveData<List<FirebaseEvent>> {
        val mutableLiveData = MutableLiveData<List<FirebaseEvent>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .get()
                .addOnSuccessListener {
                    val events = it.toObjects(FirebaseEvent::class.java)
                    val contents = events.filter { it.type.name == "Workshop" }
                    mutableLiveData.postValue(contents)
                }

        return mutableLiveData
    }

}