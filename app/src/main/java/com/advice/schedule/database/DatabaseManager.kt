package com.advice.schedule.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.advice.schedule.*
import com.advice.schedule.models.firebase.*
import com.advice.schedule.models.local.*
import com.advice.schedule.network.task.ReminderWorker
import com.advice.schedule.utilities.MyClock
import com.advice.schedule.utilities.Storage
import com.advice.schedule.utilities.now
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.BuildConfig
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


class DatabaseManager(
    private val preferences: Storage,
    private val firebaseCrashlytics: FirebaseCrashlytics
) {

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

            val defcon = list.find { it.code == "DEFCON30" }
            if (defcon?.hasFinished == false) {
                return defcon
            }

            return list.firstOrNull { !it.hasFinished }
                ?: conferences.lastOrNull()
        }
    }

    private val code
        get() = conference.value?.code ?: "DEFCON30"

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()


    val conference = MutableLiveData<Conference>()
    val conferences = MutableLiveData<List<Conference>>()

    private var user: FirebaseUser? = null

    private var tags = listOf<FirebaseTagType>()

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
                        val list = it.result.toObjectsOrEmpty(FirebaseConference::class.java)
                            .filter { !it.hidden || App.isDeveloper }
                            .mapNotNull { it.toConference() }
                            .sortedBy { it.startDate }


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
        FirebaseInstallations.getInstance()
            .getToken(false)
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
                    val selected =
                        it.result.toObjectsOrEmpty(FirebaseConference::class.java).mapNotNull { it.toConference() }.firstOrNull()
                    conference.postValue(selected)
                }
            }
    }

    fun getSchedule(): MutableLiveData<List<Event>> {
        val results = MutableLiveData<List<Event>>()
        var events = listOf<Event>()
        var bookmarks = listOf<FirebaseBookmark>()

        firebaseCrashlytics.log("fetching schedule")
        firebaseCrashlytics.setCustomKey("code", code)

        firestore.collection(CONFERENCES)
            .document(code)
            .collection(EVENTS)
            .addSnapshotListener { snapshot, exception ->
                if (snapshot != null && exception == null) {
                    events = snapshot.toObjectsOrEmpty(FirebaseEvent::class.java)
                        .filter { (!it.hidden || App.isDeveloper) }
                        .mapNotNull { it.toEvent(tags) }
                    results.postValue(mergeBookmarks(events, bookmarks))
                }
            }

        val id = user?.uid
        if (id != null) {
            firestore.collection(CONFERENCES)
                .document(code)
                .collection(USERS)
                .document(id)
                .collection(BOOKMARKS)
                .addSnapshotListener { snapshot, exception ->
                    if (snapshot != null && exception == null) {
                        bookmarks = snapshot.toObjectsOrEmpty(FirebaseBookmark::class.java)
                        results.postValue(mergeBookmarks(events, bookmarks))
                    }
                }
        }

        return results
    }

    private fun mergeBookmarks(events: List<Event>?, bookmarks: List<FirebaseBookmark>): List<Event> {
        if (events == null)
            return emptyList()

        bookmarks.forEach { bookmark ->
            events.find { it.id.toString() == bookmark.id }?.isBookmarked =
                bookmark.value
        }
        return events
    }

    fun getTags(id: Conference): LiveData<List<FirebaseTagType>> {
        val mutableLiveData = MutableLiveData<List<FirebaseTagType>>()
        var bookmarks = listOf<FirebaseBookmark>()

        firestore.collection(CONFERENCES)
            .document(id.code)
            .collection("tagtypes")
            .addSnapshotListener { snapshot, exception ->
                if (snapshot != null && exception == null) {
                    tags = snapshot.toObjectsOrEmpty(FirebaseTagType::class.java)
                        .sortedBy { it.sort_order }
                    mutableLiveData.postValue(mergeTags(tags, bookmarks))
                }
            }

        val id = user?.uid
        if (id != null) {
            firestore.collection(CONFERENCES)
                .document(code)
                .collection(USERS)
                .document(id)
                .collection(TYPES)
                .addSnapshotListener { snapshot, exception ->
                    if (snapshot != null && exception == null) {
                        bookmarks = snapshot.toObjectsOrEmpty(FirebaseBookmark::class.java)
                        mutableLiveData.postValue(mergeTags(tags, bookmarks))
                    }
                }
        }

        return mutableLiveData
    }

    private fun mergeTags(tags: List<FirebaseTagType>, bookmarks: List<FirebaseBookmark>): List<FirebaseTagType> {
        tags.forEach { type ->
            type.tags.forEach { tag ->
                tag.isSelected =
                    bookmarks.find { tag.id.toString() == it.id }?.value
                        ?: false
            }
        }
        return tags
    }

    fun getTypes(id: Conference): LiveData<List<Type>> {
        val mutableLiveData = MutableLiveData<List<Type>>()

        firestore.collection(CONFERENCES)
            .document(id.code)
            .collection(TYPES)
            .addSnapshotListener { snapshot, exception ->
                if (snapshot != null && exception == null) {
                    val types = snapshot.toObjectsOrEmpty(FirebaseType::class.java)
                        .mapNotNull { it.toType() }

                    val id = user?.uid
                    if (id != null) {
                        firestore.collection(CONFERENCES)
                            .document(code)
                            .collection(USERS)
                            .document(id)
                            .collection(TYPES)
                            .addSnapshotListener { snapshot, exception ->
                                if (snapshot != null && exception == null) {
                                    val bookmarks =
                                        snapshot.toObjectsOrEmpty(FirebaseBookmark::class.java)

                                    types.forEach { type ->
                                        type.isSelected =
                                            bookmarks.find { type.id.toString() == it.id }?.value
                                                ?: false
                                    }

                                    mutableLiveData.postValue(types)
                                } else {
                                    mutableLiveData.postValue(types)
                                }
                            }
                    } else {
                        mutableLiveData.postValue(types)
                    }
                } else {
                    mutableLiveData.postValue(emptyList())
                }
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
                if (snapshot != null && exception == null) {
                    val articles = snapshot.toObjectsOrEmpty(FirebaseArticle::class.java)
                        .filter { !it.hidden || App.isDeveloper }
                        .mapNotNull { it.toArticle() }

                    results.postValue(articles)
                } else {
                    results.postValue(emptyList())
                }
            }
        return results
    }

    fun getBookmarks(conference: Conference? = null): LiveData<List<Event>> {
        val result = MutableLiveData<List<Event>>()

        val id = user?.uid ?: return result

        firestore.collection(CONFERENCES)
            .document(conference?.code ?: code)
            .collection(EVENTS)
            .get()
            .addOnSuccessListener {
                val events = it.toObjectsOrEmpty(FirebaseEvent::class.java)
                    .filter { !it.hidden || App.isDeveloper }
                    .mapNotNull { it.toEvent(tags) }

                firestore.collection(CONFERENCES)
                    .document(code)
                    .collection(USERS)
                    .document(id)
                    .collection(BOOKMARKS)
                    .get()
                    .addOnSuccessListener {
                        val bookmarks =
                            it.toObjectsOrEmpty(FirebaseBookmark::class.java).map { it.id }

                        val bookmarked = events.filter { it.id.toString() in bookmarks }.take(3)
                        bookmarked.forEach { it.isBookmarked = true }

                        result.postValue(bookmarked)
                    }
            }
        return result
    }


    fun getFAQ(id: Conference): LiveData<List<Any>> {
        val mutableLiveData = MutableLiveData<List<Any>>()

        firestore.collection(CONFERENCES)
            .document(id.code)
            .collection(FAQS)
            .get()
            .addOnSuccessListener {
                val faqs = it.toObjectsOrEmpty(FirebaseFAQ::class.java)
                    .mapNotNull { it.toFAQ()?.toList() }
                    .flatten()
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
                if (snapshot != null && exception == null) {
                    val list =
                        snapshot.toObjectsOrEmpty(FirebaseLocation::class.java)
                            .mapNotNull { it.toLocation() }
                    mutableLiveData.postValue(list)
                } else {
                    mutableLiveData.postValue(emptyList())
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
                val vendors = it.toObjectsOrEmpty(FirebaseVendor::class.java)
                    .filter { !it.hidden || App.isDeveloper }
                    .mapNotNull { it.toVendor() }

                mutableLiveData.postValue(vendors)
            }
        return mutableLiveData
    }

    suspend fun getEventById(conference: String, id: Int): Event? {
        val snapshot = firestore.collection(CONFERENCES)
            .document(conference)
            .collection(EVENTS)
            .document(id.toString())
            .get()
            .await()

        return snapshot.toObjectOrNull(FirebaseEvent::class.java)?.toEvent(tags)
    }

    fun updateBookmark(event: Event) {
        val tag = "reminder_" + event.id

        if (event.isBookmarked) {
            val delay = event.start.toDate().time - MyClock().now().time - (1000 * 20 * 60)

            if (delay > 0) {
                val data = workDataOf(
                    ReminderWorker.INPUT_ID to event.id,
                    ReminderWorker.INPUT_CONFERENCE to event.conference
                )

                val notify = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInputData(data)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .addTag(tag)
                    .build()

                WorkManager.getInstance().enqueue(notify)
            }

        } else {
            WorkManager.getInstance().cancelAllWorkByTag(tag)
        }

        val id = user?.uid ?: return

        val document = firestore.collection(CONFERENCES)
            .document(event.conference)
            .collection(USERS)
            .document(id)
            .collection(BOOKMARKS)
            .document(event.id.toString())

        if (event.isBookmarked) {
            document.set(
                mapOf(
                    "id" to event.id.toString(),
                    "value" to true
                )
            )
        } else {
            document.delete()
        }
    }

    fun updateTypeIsSelected(type: FirebaseTag) {
        val id = user?.uid ?: return

        val document = firestore.collection(CONFERENCES)
            .document(code)
            .collection(USERS)
            .document(id)
            .collection(TYPES)
            .document(type.id.toString())

        if (type.isSelected) {
            document.set(
                mapOf(
                    "id" to type.id.toString(),
                    "value" to true
                )
            )
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


    fun getSpeakers(conference: Conference): LiveData<List<Speaker>> {
        val mutableLiveData = MutableLiveData<List<Speaker>>()

        firestore.collection(CONFERENCES)
            .document(conference.code)
            .collection(SPEAKERS)
            .addSnapshotListener { snapshot, exception ->
                if (snapshot != null && exception == null) {
                    val speakers = snapshot.toObjectsOrEmpty(FirebaseSpeaker::class.java)
                        .filter { !it.hidden || App.isDeveloper }
                        .mapNotNull { it.toSpeaker() }
                        .sortedBy { it.name.lowercase(Locale.getDefault()) }

                    mutableLiveData.postValue(speakers)
                } else {
                    mutableLiveData.postValue(emptyList())
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
                if (snapshot != null && exception == null) {
                    val events = snapshot.toObjectsOrEmpty(FirebaseEvent::class.java)
                    val filtered =
                        events.filter { it.speakers.firstOrNull { it.id == speaker.id } != null }
                            .mapNotNull { it.toEvent(tags) }
                    mutableLiveData.postValue(filtered)
                } else {
                    mutableLiveData.postValue(emptyList())
                }
            }

        return mutableLiveData
    }


    fun getMaps(conference: Conference): MutableLiveData<List<FirebaseConferenceMap>> {
        val mutableLiveData = MutableLiveData<List<FirebaseConferenceMap>>()

        val list = ArrayList<FirebaseConferenceMap>()

        val maps = conference.maps
        if (maps.isEmpty()) {
            mutableLiveData.postValue(emptyList())
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