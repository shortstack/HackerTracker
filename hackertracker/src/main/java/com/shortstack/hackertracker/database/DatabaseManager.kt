package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.toWorkData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.network.task.ReminderWorker
import com.shortstack.hackertracker.now
import io.reactivex.Single
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Chris on 3/31/2018.
 */
class DatabaseManager {

    private val userId = "user-id"

    companion object {
        private const val CONFERENCES = "conferences"

        private const val USERS = "users"
        private const val BOOKMARKS = "bookmarks"

        private const val EVENTS = "events"
        private const val TYPES = "types"
        private const val FAQS = "faqs"
        private const val VENDORS = "vendors"
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val conference = MutableLiveData<FirebaseConference>()
    val types = MutableLiveData<List<FirebaseType>>()
    val events = MutableLiveData<List<FirebaseEvent>>()
    val speakers = MutableLiveData<List<FirebaseSpeaker>>()



    init {
        if (!BuildConfig.DEBUG) {
            val settings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()

            firestore.firestoreSettings = settings
        }

        InitLoader(this)


        firestore.collection(CONFERENCES)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val cons = snapshot?.toObjects(FirebaseConference::class.java)
                        val con = cons?.firstOrNull { it.isSelected } ?: cons?.firstOrNull()

                        conference.postValue(con)

                        if (con != null) {
                            fetchConferenceTypes(con)
                        }
                    }
                }
    }

    private fun fetchConferenceTypes(con: FirebaseConference) {
        firestore.collection(CONFERENCES)
                .document(con.code)
                .collection(TYPES)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val types = snapshot?.toObjects(FirebaseType::class.java) ?: emptyList()
                        fetchUserTypes(con, types)
                    }
                }
    }

    private fun fetchUserTypes(con: FirebaseConference, list: List<FirebaseType>) {
        firestore.collection(CONFERENCES)
                .document(con.code)
                .collection(USERS)
                .document(userId)
                .collection(TYPES)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val bookmarkedTypes = snapshot?.toObjects(FirebaseBookmark::class.java) ?: emptyList()

                        bookmarkedTypes.forEach { bookmark ->
                            list.firstOrNull { it.id.toString() == bookmark.first }?.isSelected = bookmark.second
                        }
                    }

                    types.postValue(list)
                }
    }

    fun changeConference(id: Int) {
        val current = conference.value

        if (current != null) {
            firestore.collection(CONFERENCES)
                    .document(current.code)
                    .update(mapOf("is_selected" to false))
                    .addOnSuccessListener {
                        Logger.d("Removed the prev selected.")
                    }

        }

        firestore.collection(CONFERENCES)
                .whereEqualTo("id", id)
                .get()

                .addOnSuccessListener {
                    Logger.d("Added the newly selected.")

                    val selected = it.toObjects(FirebaseConference::class.java).firstOrNull()
                    if (selected != null) {
                        firestore.collection(CONFERENCES)
                                .document(selected.code)
                                .update(mapOf("is_selected" to true))
                    }
                }
    }

    fun getConferences(): LiveData<List<FirebaseConference>> {
        val mutableLiveData = MutableLiveData<List<FirebaseConference>>()

        firestore.collection(CONFERENCES)
                .addSnapshotListener { snapshot, exception ->
                    if( exception == null ) {
                        val cons = snapshot?.toObjects(FirebaseConference::class.java)
                        mutableLiveData.postValue(cons)
                    }
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

    fun getSchedule(conference: FirebaseConference, types: List<FirebaseType> = emptyList()): LiveData<List<FirebaseEvent>> {

        // TODO Handle the date of the confrence.
        val date = Date().now()

        val mutableLiveData = MutableLiveData<List<FirebaseEvent>>()

        val types = types.filter { it.isSelected }


        val timer = System.currentTimeMillis()


        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .addSnapshotListener { snapshot, exception ->

                    Logger.d("Fetched events for ${conference.name} in ${System.currentTimeMillis() - timer}ms.")

                    if (exception == null) {
                        val events = snapshot?.toObjects(FirebaseEvent::class.java) ?: emptyList()

                        val filtered = if(types.isNotEmpty())
                            events.filter { it.type in types }
                        else
                            events



                        mutableLiveData.postValue(filtered)
                    }
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

    fun getEventById(id: Int): Single<FirebaseEvent> {
        return Single.create { emitter ->
            firestore.collection(CONFERENCES)
                    .document(id.toString())
                    .get()
                    .addOnSuccessListener {
                        val event = it.toObject(FirebaseEvent::class.java)
                                ?: return@addOnSuccessListener
                        emitter.onSuccess(event)
                    }
        }
    }

    fun searchForEvents(conference: FirebaseConference, text: String): Single<List<FirebaseEvent>> {
        return Single.create { emitter ->
            val eventsRef = firestore.collection(CONFERENCES)
                    .document(conference.code)
                    .collection(EVENTS)

            eventsRef.whereGreaterThan("title", text).addSnapshotListener { snapshot, exception ->
                if (exception == null) {
                    val events = snapshot?.toObjects(FirebaseEvent::class.java)
                            ?: return@addSnapshotListener
                    Logger.d("got events!")

                    emitter.onSuccess(events)
                }
            }
        }
    }

    fun searchForLocation(conference: FirebaseConference, text: String): List<FirebaseLocation> {
        TODO("Need to implement a single threaded solution for searching.")
    }

    fun searchForSpeaker(conference: FirebaseConference, text: String): List<FirebaseSpeaker> {
        TODO("Need to implement a single threaded solution for searching.")
    }


    fun getTypeForEvent(event: FirebaseEvent?): FirebaseType? {
        if (event == null) return null

        return types.value?.firstOrNull { it.id == event.type.id }
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

        // TODO: Use the actual auth token.

        val document = firestore.collection(CONFERENCES)
                .document(event.conference)
                .collection(USERS)
                .document(userId)
                .collection(BOOKMARKS)
                .document(event.id.toString())

        if (event.isBookmarked) {
            document.set(event.id.toString() to true)
        } else {
            document.delete()
        }
    }

    fun updateTypeIsSelected(type: FirebaseType) {
        val value = types.value
        value?.find { it.id == type.id }?.isSelected = type.isSelected
        types.postValue(value)




        // TODO: Use the actual auth token.
        val document = firestore.collection(CONFERENCES)
                .document(type.conference)
                .collection(USERS)
                .document(userId)
                .collection(TYPES)
                .document(type.id.toString())

        if (type.isSelected) {
            document.set(type.id.toString() to true)
        } else {
            document.delete()
        }
    }

    fun clear() {

    }

    fun getSpeakers(event: FirebaseEvent): ArrayList<FirebaseSpeaker> {
        return event.speakers
    }

    fun getEventsForSpeaker(speaker: FirebaseSpeaker): Single<List<FirebaseEvent>> {
        return Single.create<List<FirebaseEvent>> { emitter ->

            firestore.collection(CONFERENCES)
                    .document(conference.value?.code ?: "")
                    .collection(EVENTS)
                    .whereArrayContains("speakers", speaker.name)
                    .get()
                    .addOnSuccessListener {
                        emitter.onSuccess(it.toObjects(FirebaseEvent::class.java))
                    }
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

    fun getMaps(conference: FirebaseConference): MutableLiveData<List<ConferenceMap>> {
        val mutableLiveData = MutableLiveData<List<ConferenceMap>>()

        val list = ArrayList<ConferenceMap>()

        val maps = conference.maps
        if (maps.isEmpty()) {
            mutableLiveData.postValue(emptyList())
        }

        maps.forEach {

            val temp = ConferenceMap(it.name, null)
            list.add(temp)
            mutableLiveData.postValue(list.toList())

            val map = storage.reference.child("/${conference.code}/${it.file}")

            val localFile = File.createTempFile("images", "pdf")

            map.getFile(localFile).addOnSuccessListener { task ->
                temp.file = localFile

                mutableLiveData.postValue(list.toList())
            }.addOnFailureListener {
                // Handle any errors
            }
        }

        return mutableLiveData
    }

}