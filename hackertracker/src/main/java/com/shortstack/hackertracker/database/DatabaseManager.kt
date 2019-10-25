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
import com.google.firebase.firestore.Query
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.*
import com.shortstack.hackertracker.models.firebase.*
import com.shortstack.hackertracker.models.local.*
import com.shortstack.hackertracker.network.task.ReminderWorker
import com.shortstack.hackertracker.utilities.MyClock
import com.shortstack.hackertracker.utilities.Storage
import com.shortstack.hackertracker.utilities.now
import io.reactivex.Single
import java.io.File
import java.util.concurrent.TimeUnit


class DatabaseManager(private val preferences: Storage) {

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
        private const val ARTICLES = "articles"

        fun getNextConference(preferred: Int, conferences: List<Conference>): Conference? {
            if (preferred != -1) {
                val pref = conferences.find { it.id == preferred && !it.hasFinished }
                if (pref != null) return pref
            }

            val list = conferences.sortedBy { it.startDate }

            val defcon = list.find { it.code == "DEFCON27" }
            if (defcon?.hasFinished == false) {
                return defcon
            }

            return list.firstOrNull { !it.hasFinished }
                    ?: conferences.lastOrNull()
        }
    }

    private val code
        get() = conference.value?.code ?: "DEFCON27"

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()


    val conference = MutableLiveData<Conference>()
    val conferences = MutableLiveData<List<Conference>>()

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
                            val list = it.result?.toObjects(FirebaseConference::class.java)
                                    ?.filter { !it.hidden || App.isDeveloper }
                                    ?.map { it.toConference() }
                                    ?.sortedBy { it.startDate }

                                    ?: emptyList()


                            val con = getNextConference(preferences.preferredConference, list)
                            conference.postValue(con)
                            conferences.postValue(list)

                            if (con != null)
                                getFCMToken(con)
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
        preferences.preferredConference = id

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
                        val cons = snapshot?.toObjects(FirebaseConference::class.java)
                                ?.filter { !it.hidden || App.isDeveloper }
                                ?.map { it.toConference() }

                        mutableLiveData.postValue(cons)
                    }
                }

        return mutableLiveData
    }

    fun getEvents(id: Conference, type: Type?): LiveData<List<Event>> {
        return getSchedule(type)
    }


    fun getSchedule(type: Type? = null, isTypeFiltered: Boolean = true): MutableLiveData<List<Event>> {
        val mutableLiveData = MutableLiveData<List<Event>>()

        firestore.collection(CONFERENCES)
                .document(code)
                .collection(EVENTS)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val events = snapshot?.toObjects(FirebaseEvent::class.java)
                                ?.filter { (!it.hidden || App.isDeveloper) && (!isTypeFiltered || (type == null && !it.type.filtered || type?.id == it.type.id)) }
                                ?.map { it.toEvent() }

                        mutableLiveData.postValue(events)

                        val id = user?.uid
                        if (id != null) {
                            firestore.collection(CONFERENCES)
                                    .document(code)
                                    .collection(USERS)
                                    .document(id)
                                    .collection(BOOKMARKS)
                                    .addSnapshotListener { snapshot, exception ->
                                        if (exception == null) {
                                            val bookmarks = snapshot?.toObjects(FirebaseBookmark::class.java)

                                            bookmarks?.forEach { bookmark ->
                                                events?.find { it.id.toString() == bookmark.id }?.isBookmarked = bookmark.value
                                            }

                                            mutableLiveData.postValue(events)
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
        val mutableLiveData = MutableLiveData<List<Type>>()

        firestore.collection(CONFERENCES)
                .document(code)
                .collection(TYPES)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val types = snapshot?.toObjects(FirebaseType::class.java)?.map { it.toType() }
                        mutableLiveData.postValue(types)

                        val id = user?.uid
                        if (id != null) {
                            firestore.collection(CONFERENCES)
                                    .document(code)
                                    .collection(USERS)
                                    .document(id)
                                    .collection(TYPES)
                                    .addSnapshotListener { snapshot, exception ->
                                        if (exception == null) {
                                            val bookmarks = snapshot?.toObjects(FirebaseBookmark::class.java)

                                            types?.forEach { type ->
                                                type.isSelected = bookmarks?.find { type.id.toString() == it.id }?.value
                                                        ?: false
                                            }

                                            mutableLiveData.postValue(types)
                                        }
                                    }
                        }
                    }
                }

        return mutableLiveData
    }


    fun getRecent(): LiveData<List<Event>> {
        val mutableLiveData = MutableLiveData<List<Event>>()

        firestore.collection(CONFERENCES)
                .document(code)
                .collection(EVENTS)
                .orderBy("updated_timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener {
                    val events = it.toObjects(FirebaseEvent::class.java)
                            .filter { !it.hidden || App.isDeveloper }
                            .map { it.toEvent() }

                    mutableLiveData.postValue(events)
                }

        return mutableLiveData
    }

    fun getArticles(id: Conference? = null): LiveData<List<Article>> {
        val results = MutableLiveData<List<Article>>()

        firestore.collection(CONFERENCES)
                .document(id?.code ?: code)
                .collection(ARTICLES)
                .orderBy("updated_at", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val articles = snapshot?.toObjects(FirebaseArticle::class.java)
                                ?.filter { !it.hidden || App.isDeveloper }
                                ?.map { it.toArticle() } ?: emptyList()

                        results.postValue(articles)
                    }

                }

        return results
    }

    fun getBookmarks(): LiveData<List<Event>> {
        val result = MutableLiveData<List<Event>>()

        val id = user?.uid ?: return result

        firestore.collection(CONFERENCES)
                .document(code)
                .collection(EVENTS)
                .get()
                .addOnSuccessListener {
                    val events = it.toObjects(FirebaseEvent::class.java)
                            .filter { !it.hidden || App.isDeveloper }
                            .map { it.toEvent() }

                    firestore.collection(CONFERENCES)
                            .document(code)
                            .collection(USERS)
                            .document(id)
                            .collection(BOOKMARKS)
                            .get()
                            .addOnSuccessListener {
                                val bookmarks = it.toObjects(FirebaseBookmark::class.java).map { it.id }

                                val bookmarked = events.filter { it.id.toString() in bookmarks }.take(3)
                                bookmarked.forEach { it.isBookmarked = true }

                                result.postValue(bookmarked)


                            }
                }

        return result
    }


    fun getFAQ(code: String = this.code): LiveData<List<FAQ>> {
        val mutableLiveData = MutableLiveData<List<FAQ>>()

        firestore.collection(CONFERENCES)
                .document(code)
                .collection(FAQS)
                .get()
                .addOnSuccessListener {
                    val faqs = it.toObjects(FirebaseFAQ::class.java)
                            .map { it.toFAQ() }
                    mutableLiveData.postValue(faqs)
                }

        return mutableLiveData
    }

    fun getLocations(id: Conference? = null): MutableLiveData<List<Location>> {
        val mutableLiveData = MutableLiveData<List<Location>>()

        firestore.collection(CONFERENCES)
                .document(id?.code ?: code)
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
                    val vendors = it.toObjects(FirebaseVendor::class.java)
                            .filter { !it.hidden || App.isDeveloper }
                            .map { it.toVendor() }

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
        val id = user?.uid ?: return

        val document = firestore.collection(CONFERENCES)
                .document(type.conference)
                .collection(USERS)
                .document(id)
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
                                ?.filter { !it.hidden || App.isDeveloper }
                                ?.map { it.toSpeaker() }
                                ?: emptyList()

                        mutableLiveData.postValue(speakers)
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
                                ?.filter { !it.hidden || App.isDeveloper }
                                ?.map { it.toSpeaker() }
                                ?: emptyList()

                        mutableLiveData.postValue(speakers)
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
                    val contests = it.toObjects(FirebaseEvent::class.java)
                            .filter { (!it.hidden || App.isDeveloper) && it.type.name == "Contest" }
                            .map { it.toEvent() }

                    mutableLiveData.postValue(contests)
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
                    val workshops = it.toObjects(FirebaseEvent::class.java)
                            .filter { (!it.hidden || App.isDeveloper) && it.type.name == "Workshop" }
                            .map { it.toEvent() }

                    mutableLiveData.postValue(workshops)
                }

        return mutableLiveData
    }

    fun getMaps(conference: Conference): MutableLiveData<List<FirebaseConferenceMap>> {
        val mutableLiveData = MutableLiveData<List<FirebaseConferenceMap>>()

        val list = ArrayList<FirebaseConferenceMap>()

        val maps = conference.maps
        if (maps.isEmpty()) {
            return mutableLiveData
        }

        maps.forEach {
            val map = FirebaseConferenceMap(it.name, it.file, null)
            list.add(map)
        }

        mutableLiveData.postValue(list)

        list.forEach {
            val filename = "${conference.code}-${it.path}"
            val file = File(App.instance.applicationContext.getExternalFilesDir(null), filename)
            if (file.exists()) {
                it.file = file
            } else {
                file.createNewFile()

                val map = storage.reference.child("/${conference.code}/${it.path}")

                map.getFile(file).addOnSuccessListener { task ->
                    it.file = file

                    mutableLiveData.postValue(list.toList())
                }.addOnFailureListener {
                    // Handle any errors
                }
            }
        }
        return mutableLiveData
    }


}