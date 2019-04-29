package com.shortstack.hackertracker.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.toWorkData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.iid.FirebaseInstanceId
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
    val locations = MutableLiveData<List<FirebaseLocation>>()


    lateinit var user: FirebaseUser

    init {
        if (!BuildConfig.DEBUG) {
            val settings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()

            firestore.firestoreSettings = settings
        }

        auth.signInAnonymously().addOnCompleteListener {
            user = it.result?.user ?: return@addOnCompleteListener

            InitLoader(this@DatabaseManager) {
                getFCMToken(it)
            }
        }


    }

    private fun getFCMToken(conference: FirebaseConference) {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Logger.e(task.exception, "Could not get token.")
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token
                    Logger.d("Obtained token: $token")
                    updateFirebaseMessagingToken(conference, token)
                })
    }

    fun changeConference(id: Int) {
        val current = conference.value

        if (current != null) {
            current.isSelected = false
        }

        firestore.collection(CONFERENCES)
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val selected = it.result?.toObjects(FirebaseConference::class.java)?.firstOrNull()
                        InitLoader(this, selected)
                    }
                }
    }

    fun getConferences(): LiveData<List<FirebaseConference>> {
        val mutableLiveData = MutableLiveData<List<FirebaseConference>>()

        firestore.collection(CONFERENCES)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
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

    fun findEvents(text: String): List<FirebaseEvent> {
        return events.value?.filter { it.title.contains(text, true) } ?: emptyList()
    }

    fun findLocation(text: String): List<FirebaseLocation> {
        return locations.value?.filter { it.name.contains(text, true) } ?: emptyList()
    }

    fun findSpeaker(text: String): List<FirebaseSpeaker> {
        return speakers.value?.filter { it.name.contains(text, true) } ?: emptyList()
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

        val document = firestore.collection(CONFERENCES)
                .document(event.conference)
                .collection(USERS)
                .document(user.uid)
                .collection(BOOKMARKS)
                .document(event.id.toString())

        if (event.isBookmarked) {
            document.set(mapOf("id" to event.id.toString(),
                    "value" to true))
        } else {
            document.delete()
        }
    }

    fun updateTypeIsSelected(type: FirebaseType) {
        val value = types.value
        value?.find { it.id == type.id }?.isSelected = type.isSelected
        types.postValue(value)


        val document = firestore.collection(CONFERENCES)
                .document(type.conference)
                .collection(USERS)
                .document(user.uid)
                .collection(TYPES)
                .document(type.id.toString())

        if (type.isSelected) {
            document.set(mapOf("id" to type.id.toString(),
                    "value" to true))
        } else {
            document.delete()
        }
    }

    private fun updateFirebaseMessagingToken(conference: FirebaseConference?, token: String?) {
        if (conference == null || token == null) {
            Log.e("TAG", "Null, cannot update token.")
            return
        }

        val document = firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(USERS)
                .document(user.uid)


        document.set(mapOf("token" to token))
    }

    fun clear() {

    }

    fun getSpeakers(event: FirebaseEvent): ArrayList<FirebaseSpeaker> {
        return event.speakers
    }

    fun getEventsForSpeaker(speaker: FirebaseSpeaker): Single<List<FirebaseEvent>> {
        return Single.create<List<FirebaseEvent>> { emitter ->
            emitter.onSuccess(events.value?.filter { it.speakers.contains(speaker) } ?: emptyList())
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