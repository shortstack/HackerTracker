package com.shortstack.hackertracker.database

import com.google.firebase.firestore.FirebaseFirestore
import com.shortstack.hackertracker.models.firebase.*
import com.shortstack.hackertracker.models.local.Conference
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.toConference
import com.shortstack.hackertracker.toEvent

class InitLoader(private val database: DatabaseManager, conference: Conference? = null, private val onComplete: ((Conference) -> Unit)? = null) {

    companion object {
        private const val CONFERENCES = "conferences"

        private const val USERS = "users"
        private const val BOOKMARKS = "bookmarks"

        private const val EVENTS = "events"
        private const val TYPES = "types"
        private const val LOCATIONS = "locations"
        private const val FAQS = "faqs"
        private const val SPEAKERS = "speakers"
        private const val VENDORS = "vendors"
    }

    private val firestore = FirebaseFirestore.getInstance()


    private val conferences = ArrayList<Conference>()

    private val types = ArrayList<FirebaseType>()
    private val events = ArrayList<Event>()
    private val speakers = ArrayList<FirebaseSpeaker>()
    private val locations = ArrayList<FirebaseLocation>()

    init {
        if (conference != null) {
            getConferenceDetails(conference)
        } else {
            firestore.collection(CONFERENCES)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val cons = it.result?.toObjects(FirebaseConference::class.java)
                                    ?: emptyList()
                            conferences.addAll(cons.map { it.toConference() })

                            // TODO: get the selected con
                            val selected = cons.find { it.code == "THOTCON0xA" }
                                    ?: cons.firstOrNull()

                            if (selected != null) {
                                getConferenceDetails(selected.toConference())
                            }
                        }
                    }
        }
    }

    private fun getConferenceDetails(conference: Conference) {
        database.conference.postValue(conference)

        getTypes(conference)
        getEvents(conference)
        getSpeakers(conference)
        getLocations(conference)
    }

    private fun getLocations(conference: Conference) {
        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(LOCATIONS)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val list = snapshot?.toObjects(FirebaseLocation::class.java)
                                ?: emptyList()

                        locations.clear()
                        locations.addAll(list)
                    }
                }
    }

    private fun getSpeakers(conference: Conference) {
        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(SPEAKERS)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val list = snapshot?.toObjects(FirebaseSpeaker::class.java)
                                ?: emptyList()

                        speakers.clear()
                        speakers.addAll(list)

                        onSuccess(conference)
                    }
                }
    }

    private fun getEvents(conference: Conference) {
        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val list = snapshot?.toObjects(FirebaseEvent::class.java) ?: emptyList()

                        events.clear()
                        events.addAll(list.map { it.toEvent() }.filter { !it.hasFinished })

                        firestore.collection(CONFERENCES)
                                .document(conference.code)
                                .collection(USERS)
                                .document(database.user.uid)
                                .collection(BOOKMARKS)
                                .get()
                                .addOnCompleteListener {

                                    val list = it.result?.toObjects(FirebaseBookmark::class.java)
                                            ?: emptyList()

                                    list.forEach { bookmark ->
                                        events.find { it.id.toString() == bookmark.id }?.isBookmarked = bookmark.value
                                    }

                                    onSuccess(conference)

                                }


                    }
                }
    }

    private fun getTypes(conference: Conference) {
        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(TYPES)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val list = snapshot?.toObjects(FirebaseType::class.java) ?: emptyList()

                        types.clear()
                        types.addAll(list)

                        firestore.collection(CONFERENCES)
                                .document(conference.code)
                                .collection(USERS)
                                .document(database.user.uid)
                                .collection(TYPES)
                                .get()
                                .addOnCompleteListener {

                                    val list = it.result?.toObjects(FirebaseBookmark::class.java)
                                            ?: emptyList()

                                    list.forEach { bookmark ->
                                        types.find { it.id.toString() == bookmark.id }?.isSelected = bookmark.value
                                    }

                                    onSuccess(conference)

                                }
                    }
                }
    }


    private fun onSuccess(conference: Conference) {
        if (events.isNotEmpty() && types.isNotEmpty() && speakers.isNotEmpty()) {
            database.conference.postValue(conference)
            database.types.postValue(types)
            database.events.postValue(events)
            database.speakers.postValue(speakers)
            database.locations.postValue(locations)

            onComplete?.invoke(conference)
        }
    }
}