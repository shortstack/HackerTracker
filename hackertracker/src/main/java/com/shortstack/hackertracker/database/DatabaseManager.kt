package com.shortstack.hackertracker.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

        private const val CURRENT_CON = "CACKALACKY2019"
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()


    val conference = MutableLiveData<Conference>()
    lateinit var user: FirebaseUser



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
                    .document(CURRENT_CON)
                    .addSnapshotListener { snapshot, exception ->
                        if( exception == null ) {
                            val cack = snapshot?.toObject(FirebaseConference::class.java)?.toConference()
                            conference.postValue(cack)

                            if( cack != null )
                            getFCMToken(cack)
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

    fun getSchedule(): MutableLiveData<List<Event>> {
        val mutableLiveData = MutableLiveData<List<Event>>()

        firestore.collection(CONFERENCES)
                .document(CURRENT_CON)
                .collection(EVENTS)
                .addSnapshotListener{ snapshot, exception ->
                    if(exception == null) {
                        val events = snapshot?.toObjects(FirebaseEvent::class.java)
                        val recent = events?.map { it.toEvent() }
                        mutableLiveData.postValue(recent)

                        firestore.collection(CONFERENCES)
                                .document(CURRENT_CON)
                                .collection(USERS)
                                .document(user.uid)
                                .collection(BOOKMARKS)
                                .addSnapshotListener{ snapshot, exception ->
                                    if(exception == null) {
                                        val events = snapshot?.toObjects(FirebaseBookmark::class.java)

                                        events?.forEach {  bookmark ->
                                            recent?.find { it.id.toString() == bookmark.id }?.isBookmarked = bookmark.value
                                        }

                                        mutableLiveData.postValue(recent)
                                    }
                                }
                    }
                }

        return mutableLiveData
    }

    fun getScheduleTypes(): MutableLiveData<List<Type>> {
        val mutableLiveData = MutableLiveData<List<Type>>()

        firestore.collection(CONFERENCES)
                .document(CURRENT_CON)
                .collection(TYPES)
                .addSnapshotListener{ snapshot, exception ->
                    if(exception == null) {
                        val types = snapshot?.toObjects(FirebaseType::class.java)?.map { it.toType() }
                        mutableLiveData.postValue(types)

                        firestore.collection(CONFERENCES)
                                .document(CURRENT_CON)
                                .collection(USERS)
                                .document(user.uid)
                                .collection(TYPES)
                                .addSnapshotListener{ snapshot, exception ->
                                    if(exception == null) {
                                        val bookmarks = snapshot?.toObjects(FirebaseBookmark::class.java)

                                        types?.forEach { type ->
                                            type.isSelected = bookmarks?.find { type.id.toString() == it.id }?.value ?: false
                                        }

                                        mutableLiveData.postValue(types)
                                    }
                                }
                    }
                }

        return mutableLiveData
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
                .document(CURRENT_CON)
                .collection(LOCATIONS)
                .addSnapshotListener { snapshot, exception ->
                    if(exception == null) {
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

    fun updateTypeIsSelected(type: Type) {
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

    private fun updateFirebaseMessagingToken(conference: Conference?, token: String?) {
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

    fun getSpeakers(): LiveData<List<Speaker>> {
        val mutableLiveData = MutableLiveData<List<Speaker>>()

        firestore.collection(CONFERENCES)
                .document(CURRENT_CON)
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

    fun getEventsForSpeaker(speaker: Speaker): Single<List<Event>> {
        return Single.create<List<Event>> { emitter ->
            TODO()
//            emitter.onSuccess(events.value?.filter { it.speakers.contains(speaker) } ?: emptyList())
        }
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