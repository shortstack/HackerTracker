package com.shortstack.hackertracker.database

import com.google.firebase.firestore.FirebaseFirestore
import com.shortstack.hackertracker.models.*

class InitLoader(private val database: DatabaseManager) {

    companion object {
        private const val CONFERENCES = "conferences"

        private const val USERS = "users"
        private const val BOOKMARKS = "bookmarks"

        private const val EVENTS = "events"
        private const val TYPES = "types"
        private const val FAQS = "faqs"
        private const val SPEAKERS = "speakers"
        private const val VENDORS = "vendors"
    }

    private val firestore = FirebaseFirestore.getInstance()


    private val conferences = ArrayList<FirebaseConference>()

    private val types = ArrayList<FirebaseType>()
    private val events = ArrayList<FirebaseEvent>()
    private val speakers = ArrayList<FirebaseSpeaker>()

    init {
        firestore.collection(CONFERENCES)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val cons = it.result?.toObjects(FirebaseConference::class.java)
                                ?: emptyList()
                        conferences.addAll(cons)

                        // TODO: get the selected con
                        val selected = cons.find { it.code == "THOTCON0xA" } ?: cons.firstOrNull()

                        if (selected != null) {
                            getTypes(selected)
                            getEvents(selected)
                            getSpeakers(selected)
                        }
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

                        onSuccess()
                    }
                }
    }

    private fun getEvents(conference: FirebaseConference) {
        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(EVENTS)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val list = it.result?.toObjects(FirebaseEvent::class.java) ?: emptyList()
                        events.addAll(list)

                        firestore.collection(CONFERENCES)
                                .document(conference.code)
                                .collection(USERS)
                                .document("user-id")
                                .collection(BOOKMARKS)
                                .get()
                                .addOnCompleteListener {

                                    val list = it.result?.toObjects(FirebaseBookmark::class.java)
                                            ?: emptyList()

                                    list.forEach { bookmark ->
                                        events.find { it.id.toString() == bookmark.first }?.isBookmarked = bookmark.second
                                    }

                                    onSuccess()

                                }


                    }
                }
    }

    private fun getTypes(conference: FirebaseConference) {
        firestore.collection(CONFERENCES)
                .document(conference.code)
                .collection(TYPES)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val list = it.result?.toObjects(FirebaseType::class.java) ?: emptyList()
                        types.addAll(list)

                        firestore.collection(CONFERENCES)
                                .document(conference.code)
                                .collection(USERS)
                                .document("user-id")
                                .collection(TYPES)
                                .get()
                                .addOnCompleteListener {

                                    val list = it.result?.toObjects(FirebaseBookmark::class.java)
                                            ?: emptyList()

                                    list.forEach { bookmark ->
                                        types.find { it.id.toString() == bookmark.first }?.isSelected = bookmark.second
                                    }

                                    onSuccess()

                                }
                    }
                }
    }


    private fun onSuccess() {
        if (events.isNotEmpty() && types.isNotEmpty() && speakers.isNotEmpty()) {
            database.conference.postValue(conferences.first())
            database.types.postValue(types)
            database.events.postValue(events)
            database.speakers.postValue(speakers)
        }
    }


}