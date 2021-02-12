package com.shortstack.hackertracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.firebase.FirebaseConferenceMap
import com.shortstack.hackertracker.models.local.*
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage
import org.koin.core.KoinComponent
import org.koin.core.inject

class HackerTrackerViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()
    private val storage: Storage by inject()
    private val themes: ThemesManager by inject()


    val conference: LiveData<Resource<Conference>>
    val events: LiveData<Resource<List<Event>>>
    val bookmarks: LiveData<Resource<List<Event>>>
    val types: LiveData<Resource<List<Type>>>
    val locations: LiveData<Resource<List<Location>>>
    val speakers: LiveData<Resource<List<Speaker>>>

    val articles: LiveData<Resource<List<Article>>>
    val faq: LiveData<Resource<List<FAQ>>>
    val vendors: LiveData<Resource<List<Vendor>>>

    val maps: LiveData<Resource<List<FirebaseConferenceMap>>>

    // Home
    val home: LiveData<Resource<List<Any>>>

    // Schedule
    val schedule: LiveData<Resource<List<Event>>>


    // Search
    private val query = MediatorLiveData<String>()
    val search: LiveData<List<Any>>


    init {
        conference = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<Conference>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.value = Resource.success(it)
            }


            return@switchMap result
        }

        types = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Type>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.addSource(database.getTypes(it)) {
                    result.value = Resource.success(it)
                }
            }
            return@switchMap result
        }

        locations = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Location>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.addSource(database.getLocations(it)) {
                    result.value = Resource.success(it)
                }
            }
            return@switchMap result
        }

        events = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Event>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.addSource(database.getSchedule()) {
                    result.value = Resource.success(it)
                }
            }


            return@switchMap result
        }

        schedule = Transformations.switchMap(database.conference) { id ->
            val result = MediatorLiveData<Resource<List<Event>>>()

            if (id == null) {
                result.value = Resource.init(null)
                return@switchMap result
            }

            result.value = Resource.loading(null)

            result.addSource(events) {
                val types = types.value?.data ?: emptyList()
                result.value = Resource.success(getSchedule(it?.data ?: emptyList(), types))
            }

            result.addSource(types) { types ->
                val events = events.value?.data ?: return@addSource
                result.value = Resource.success(getSchedule(events, types?.data ?: emptyList()))
            }

            return@switchMap result
        }

        bookmarks = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Event>>>()

            if (it == null) {
                result.value = Resource.init(null)
                return@switchMap result
            } else {
                result.addSource(database.getBookmarks(it)) {
                    result.value = Resource.success(it)
                }
            }



            return@switchMap result
        }


        speakers = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Speaker>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.addSource(database.getSpeakers(it)) {
                    result.value = Resource.success(it)
                }
            }


            return@switchMap result
        }

        articles = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Article>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.addSource(database.getArticles(it)) {
                    result.value = Resource.success(it)
                }
            }


            return@switchMap result
        }

        faq = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<FAQ>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.addSource(database.getFAQ(it)) {
                    result.value = Resource.success(it)
                }
            }

            return@switchMap result
        }

        vendors = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Vendor>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.addSource(database.getVendors(it)) {
                    result.value = Resource.success(it)
                }
            }

            return@switchMap result
        }

        maps = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<FirebaseConferenceMap>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.addSource(database.getMaps(it)) {
                    result.value = Resource.success(it)
                }
            }

            return@switchMap result
        }

        search = Transformations.switchMap(query) { text ->
            val results = MediatorLiveData<List<Any>>()

            results.addSource(events) {
                val locations = locations.value?.data ?: emptyList()
                val speakers = speakers.value?.data ?: emptyList()
                setValue(results, text, it?.data ?: emptyList(), locations, speakers)
            }

            results.addSource(locations) {
                val events = events.value?.data ?: emptyList()
                val speakers = speakers.value?.data ?: emptyList()
                setValue(results, text, events, it?.data ?: emptyList(), speakers)
            }

            results.addSource(speakers) {
                val events = events.value?.data ?: emptyList()
                val locations = locations.value?.data ?: emptyList()
                setValue(results, text, events, locations, it?.data ?: emptyList())
            }

            return@switchMap results
        }

        home = Transformations.switchMap(database.conference) { id ->
            val result = MediatorLiveData<Resource<List<Any>>>()

            if (id == null) {
                result.value = Resource.init(null)
                return@switchMap result
            }

            result.value = Resource.loading(null)

            result.addSource(bookmarks) {
                val articles = articles.value?.data?.take(4) ?: emptyList()
                result.value = Resource.success(articles + (it.data?.take(3) ?: emptyList()))
            }

            result.addSource(articles) {
                val bookmarks = bookmarks.value?.data?.take(3) ?: emptyList()
                result.value = Resource.success((it.data?.take(4) ?: emptyList()) + bookmarks)
            }

            return@switchMap result
        }

    }

    private fun getSchedule(events: List<Event>, types: List<Type>): List<Event> {
        if (types.isEmpty())
            return events

        val requireBookmark = types.firstOrNull { it.isBookmark }?.isSelected ?: false
        val filter = types.filter { !it.isBookmark && it.isSelected }
        if (!requireBookmark && filter.isEmpty())
            return events

        if (requireBookmark && filter.isEmpty())
            return events.filter { it.isBookmarked }

        return events.filter { event -> isShown(event, requireBookmark, filter) }
    }

    private fun isShown(event: Event, requireBookmark: Boolean, filter: List<Type>): Boolean {
        val bookmark = if (requireBookmark) {
            event.isBookmarked
        } else {
            true
        }

        return bookmark && filter.find { it.id == event.type.id }?.isSelected == true
    }

    private fun setValue(
        results: MediatorLiveData<List<Any>>,
        query: String,
        events: List<Event>,
        locations: List<Location>,
        speakers: List<Speaker>
    ) {
        if (query.isBlank()) {
            results.value = emptyList()
            return
        }

        val list = ArrayList<Any>()

        val speakers = speakers.filter {
            it.name.contains(query, true) || it.description.contains(
                query,
                true
            )
        }
        if (speakers.isNotEmpty()) {
            list.add("Speakers")
            list.addAll(speakers)
        }

        val locations = locations.filter { it.name.contains(query, true) }
        locations.forEach { location ->
            list.add(location)
            // TODO: Should we add the filtered events, or all events for this location?
            list.addAll(events.filter { it.location.name == location.name }.sortedBy { it.start })
        }

        val events =
            events.filter { it.title.contains(query, true) || it.description.contains(query, true) }
        if (events.isNotEmpty()) {
            list.add("Events")
            list.addAll(events)
        }

        results.value = list
    }


    fun onQueryTextChange(text: String?) {
        query.value = text
    }

}