package com.shortstack.hackertracker.database

import com.google.firebase.firestore.FirebaseFirestore
import com.shortstack.hackertracker.models.*

class InitLoader(private val database: DatabaseManager, conference: FirebaseConference? = null, private val onComplete: ((FirebaseConference) -> Unit)? = null) {

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


    private val conferences = ArrayList<FirebaseConference>()

    private val types = ArrayList<FirebaseType>()
    private val events = ArrayList<FirebaseEvent>()
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
                            conferences.addAll(cons)

                            // TODO: get the selected con
                            val selected = cons.find { it.code == "THOTCON0xA" }
                                    ?: cons.firstOrNull()

                            if (selected != null) {
                                getConferenceDetails(selected)
                            }
                        }
                    }
        }
    }

    private fun getConferenceDetails(conference: FirebaseConference) {
        getTypes(conference)
        getEvents(conference)
        getSpeakers(conference)
        getLocations(conference)
    }

    private fun getLocations(conference: FirebaseConference) {
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

    private fun getSpeakers(conference: FirebaseConference) {
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

    private fun getEvents(conference: FirebaseConference) {
        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        val list = snapshot?.toObjects(FirebaseEvent::class.java) ?: emptyList()

                        events.clear()
                        events.addAll(list)

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
                                        events.find { it.id.toString() == bookmark.first }?.isBookmarked = bookmark.second
                                    }

                                    onSuccess(conference)

                                }


                    }
                }
    }

    private fun getTypes(conference: FirebaseConference) {
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
                                        types.find { it.id.toString() == bookmark.first }?.isSelected = bookmark.second
                                    }

                                    onSuccess(conference)

                                }
                    }
                }
    }


    private fun onSuccess(conference: FirebaseConference) {
        if (events.isNotEmpty() && types.isNotEmpty() && speakers.isNotEmpty()) {
            database.conference.postValue(conference)
            database.types.postValue(types)
            database.events.postValue(events)
            database.speakers.postValue(speakers)

            onComplete?.invoke(conference)
        }
    }
}