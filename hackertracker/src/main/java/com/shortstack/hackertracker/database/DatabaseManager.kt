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
import com.shortstack.hackertracker.*
import com.shortstack.hackertracker.models.firebase.*
import com.shortstack.hackertracker.models.local.*
import com.shortstack.hackertracker.network.task.ReminderWorker
import com.shortstack.hackertracker.utils.MyClock
import com.shortstack.hackertracker.utils.now
import io.reactivex.Single
import java.io.File
import java.util.concurrent.TimeUnit


class DatabaseManager {

    companion object {
        private const val CONFERENCES = "conferences"

        private const val USERS = "users"
        private const val BOOKMARKS = "bookmarks"

        private const val EVENTS = "events"
        private const val TYPES = "types"
        private const val FAQS = "faqs"
        private const val VENDORS = "vendors"
        private const val SPEAKERS = "speakers"
        private const val LOCATIONS = "locations"

        fun getNextConference(conferences: List<Conference>): Conference? {
            return conferences.sortedBy { it.startDate }.firstOrNull { !it.hasFinished }
                    ?: conferences.lastOrNull()
        }
    }

    private val code
        get() = conference.value?.code ?: "LAYERONE2019"

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()


    val conference = MutableLiveData<Conference>()
    private var types = MutableLiveData<List<Type>>()

    private var user: FirebaseUser? = null

    init {
        if (!BuildConfig.DEBUG) {
            val settings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()

            firestore.firestoreSettings = settings
        }

        auth.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                user = it.result?.user ?: return@addOnCompleteListener
            }

            firestore.collection(CONFERENCES)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val conferences = it.result?.toObjects(FirebaseConference::class.java)?.map { it.toConference() }?.sortedBy { it.startDate }
                                    ?: emptyList()

                            val con = getNextConference(conferences)
                            conference.postValue(con)



                            if (con != null) {
                                getFCMToken(con)

                                types = MutableLiveData()

                                firestore.collection(CONFERENCES)
                                        .document(con.code)
                                        .collection(TYPES)
                                        .addSnapshotListener { snapshot, exception ->
                                            if (exception == null) {
                                                val list = snapshot?.toObjects(FirebaseType::class.java)?.map { it.toType() }
                                                types.postValue(list)
                                            }
                                        }
                            }
                        }
                    }
        }
    }


    private fun getFCMToken(conference: Conference) {
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
                        val selected = it.result?.toObjects(FirebaseConference::class.java)?.firstOrNull()?.toConference()
                        conference.postValue(selected)
                    }
                }
    }

    fun getConferences(): LiveData<List<Conference>> {
        val mutableLiveData = MutableLiveData<List<Conference>>()

        firestore.collection(CONFERENCES)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val cons = snapshot?.toObjects(FirebaseConference::class.java)?.map { it.toConference() }
                        mutableLiveData.postValue(cons)
                    }
                }

        return mutableLiveData
    }

    fun getEvents(id: Conference): LiveData<List<Event>> {
        return getSchedule()
    }

    fun getSchedule(): MutableLiveData<List<Event>> {
        val mutableLiveData = MutableLiveData<List<Event>>()

        firestore.collection(CONFERENCES)
                .document(code)
                .collection(EVENTS)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val events = snapshot?.toObjects(FirebaseEvent::class.java)
                        val recent = events?.map { it.toEvent() }
                        mutableLiveData.postValue(recent)

                        val id = user?.uid
                        if (id != null) {
                            firestore.collection(CONFERENCES)
                                    .document(code)
                                    .collection(USERS)
                                    .document(id)
                                    .collection(BOOKMARKS)
                                    .addSnapshotListener { snapshot, exception ->
                                        if (exception == null) {
                                            val events = snapshot?.toObjects(FirebaseBookmark::class.java)

                                            events?.forEach { bookmark ->
                                                recent?.find { it.id.toString() == bookmark.id }?.isBookmarked = bookmark.value
                                            }

                                            mutableLiveData.postValue(recent)
                                        }
                                    }
                        }
                    }
                }

        return mutableLiveData
    }

    fun getTypes(id: Conference): LiveData<List<Type>> {
        return getScheduleTypes()
    }

    fun getScheduleTypes(): MutableLiveData<List<Type>> {


        return types
    }


    fun getRecent(conference: Conference): LiveData<List<Event>> {
        val mutableLiveData = MutableLiveData<List<Event>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .get()
                .addOnSuccessListener {
                    val events = it.toObjects(FirebaseEvent::class.java)
                    val recent = events.map { it.toEvent() }.sortedBy { it.updated }.take(10)
                    mutableLiveData.postValue(recent)
                }

        return mutableLiveData
    }

    fun getFAQ(conference: Conference): LiveData<List<FirebaseFAQ>> {
        val mutableLiveData = MutableLiveData<List<FirebaseFAQ>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(FAQS)
                .get()
                .addOnSuccessListener {
                    val faqs = it.toObjects(FirebaseFAQ::class.java)
                    mutableLiveData.postValue(faqs)
                }

        return mutableLiveData
    }

    fun getLocations(): MutableLiveData<List<Location>> {
        val mutableLiveData = MutableLiveData<List<Location>>()

        firestore.collection(CONFERENCES)
                .document(code)
                .collection(LOCATIONS)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val list = snapshot?.toObjects(FirebaseLocation::class.java)?.map { it.toLocation() }
                        mutableLiveData.postValue(list)
                    }
                }

        return mutableLiveData
    }

    fun getVendors(conference: Conference): LiveData<List<Vendor>> {
        val mutableLiveData = MutableLiveData<List<Vendor>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(VENDORS)
                .get()
                .addOnSuccessListener {
                    val vendors = it.toObjects(FirebaseVendor::class.java).map { it.toVendor() }
                    mutableLiveData.postValue(vendors)
                }
        return mutableLiveData
    }

    fun getEventById(id: Int): Single<Event> {
        return Single.create { emitter ->
            firestore.collection(CONFERENCES)
                    .document(id.toString())
                    .get()
                    .addOnSuccessListener {
                        val event = it.toObject(FirebaseEvent::class.java)
                                ?: return@addOnSuccessListener
                        emitter.onSuccess(event.toEvent())
                    }
        }
    }

    fun updateBookmark(event: Event) {
        val tag = "reminder_" + event.id

        if (event.isBookmarked) {
            val delay = event.start.time - MyClock().now().time - (1000 * 20 * 60)

            if (delay > 0) {
                val notify = OneTimeWorkRequestBuilder<ReminderWorker>()
                        .setInputData(mapOf(ReminderWorker.NOTIFICATION_ID to event.id).toWorkData())
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .addTag(tag)
                        .build()

                WorkManager.getInstance()?.enqueue(notify)
            }

        } else {
            WorkManager.getInstance()?.cancelAllWorkByTag(tag)
        }

        val id = user?.uid ?: return

        val document = firestore.collection(CONFERENCES)
                .document(event.conference)
                .collection(USERS)
                .document(id)
                .collection(BOOKMARKS)
                .document(event.id.toString())

        if (event.isBookmarked) {
            document.set(mapOf("id" to event.id.toString(),
                    "value" to true))
        } else {
            document.delete()
        }
    }

    fun updateTypeIsSelected(type: Type) {
        val value = types.value
        value?.find { it.id == type.id }?.isSelected = type.isSelected
        types.postValue(value)
    }

    private fun updateFirebaseMessagingToken(conference: Conference?, token: String?) {
        val id = user?.uid

        if (conference == null || token == null || id == null) {
            Log.e("TAG", "Null, cannot update token.")
            return
        }

        val document = firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(USERS)
                .document(id)


        document.set(mapOf("token" to token))
    }

    fun clear() {

    }

    fun getSpeakers(): LiveData<List<Speaker>> {
        val mutableLiveData = MutableLiveData<List<Speaker>>()

        firestore.collection(CONFERENCES)
                .document(code)
                .collection(SPEAKERS)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val speakers = snapshot?.toObjects(FirebaseSpeaker::class.java)
                                ?: emptyList()
                        mutableLiveData.postValue(speakers.map { it.toSpeaker() })
                    }
                }

        return mutableLiveData
    }


    fun getSpeakers(conference: Conference): LiveData<List<Speaker>> {
        val mutableLiveData = MutableLiveData<List<Speaker>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(SPEAKERS)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val speakers = snapshot?.toObjects(FirebaseSpeaker::class.java)
                                ?: emptyList()
                        mutableLiveData.postValue(speakers.map { it.toSpeaker() })
                    }
                }

        return mutableLiveData
    }

    fun getEventsForSpeaker(speaker: Speaker): LiveData<List<Event>> {
        val mutableLiveData = MutableLiveData<List<Event>>()

        firestore.collection(CONFERENCES)
                .document(code)
                .collection(EVENTS)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val events = snapshot?.toObjects(FirebaseEvent::class.java)
                        val filtered = events?.filter { it.speakers.firstOrNull { it.id == speaker.id } != null }?.map { it.toEvent() }
                        mutableLiveData.postValue(filtered)
                    }
                }

        return mutableLiveData
    }


    fun getContests(conference: Conference): MutableLiveData<List<Event>> {
        val mutableLiveData = MutableLiveData<List<Event>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .get()
                .addOnSuccessListener {
                    val events = it.toObjects(FirebaseEvent::class.java)

                    val contents = events.filter { it.type.name == "Contest" }.map { it.toEvent() }
                    mutableLiveData.postValue(contents)
                }

        return mutableLiveData
    }

    fun getWorkshops(conference: Conference): MutableLiveData<List<Event>> {
        val mutableLiveData = MutableLiveData<List<Event>>()

        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .get()
                .addOnSuccessListener {
                    val events = it.toObjects(FirebaseEvent::class.java)
                    val contents = events.filter { it.type.name == "Workshop" }.map { it.toEvent() }
                    mutableLiveData.postValue(contents)
                }

        return mutableLiveData
    }

    fun getMaps(conference: Conference): MutableLiveData<List<FirebaseConferenceMap>> {
        val mutableLiveData = MutableLiveData<List<FirebaseConferenceMap>>()

        val list = ArrayList<FirebaseConferenceMap>()

        val maps = conference.maps
        if (maps.isEmpty()) {
            mutableLiveData.postValue(emptyList())
        }

        maps.forEach {

            val temp = FirebaseConferenceMap(it.name, null)
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