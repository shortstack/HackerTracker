package com.advice.schedule.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.advice.schedule.Resource
import com.advice.schedule.Response
import com.advice.schedule.dObj
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.firebase.FirebaseConferenceMap
import com.advice.schedule.models.local.*
import com.advice.schedule.ui.themes.ThemesManager
import com.advice.schedule.utilities.Storage
import org.koin.core.KoinComponent
import org.koin.core.inject

class HackerTrackerViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val conference: LiveData<Resource<Conference>>
    val events: LiveData<Resource<List<Event>>>
    val bookmarks: LiveData<Resource<List<Event>>>
    val types: LiveData<Resource<List<Type>>>
    val locations: LiveData<Resource<List<Location>>>
    val speakers: LiveData<Response<List<Speaker>>>

    val articles: LiveData<Resource<List<Article>>>
    val faq: LiveData<Response<List<FAQ>>>
    val vendors: LiveData<Response<List<Vendor>>>

    val maps: LiveData<Resource<List<FirebaseConferenceMap>>>

    // Home
    val home: LiveData<Resource<List<Any>>>

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
            val result = MediatorLiveData<Response<List<Speaker>>>()

            if (it == null) {
                result.value = Response.Init
            } else {
                result.addSource(database.getSpeakers(it)) {
                    result.value = Response.Success(it)
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
            val result = MediatorLiveData<Response<List<FAQ>>>()

            if (it == null) {
                result.value = Response.Loading
            } else {
                result.addSource(database.getFAQ(it)) {
                    result.value = Response.Success(it)
                }
            }

            return@switchMap result
        }

        vendors = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Response<List<Vendor>>>()

            if (it == null) {
                result.value = Response.Init
            } else {
                result.addSource(database.getVendors(it)) {
                    result.value = Response.Success(it)
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

//            results.addSource(events) {
//                val locations = locations.value?.data ?: emptyList()
//                val speakers = speakers.value?.dObj ?: emptyList()
//                setValue(results, text, it?.data ?: emptyList(), locations, speakers)
//            }
//
//            results.addSource(locations) {
//                val events = events.value?.data ?: emptyList()
//                val speakers = speakers.value?.dObj ?: emptyList<Speaker>()
//                setValue(results, text, events, it?.data ?: emptyList(), speakers)
//            }
//
//            results.addSource(speakers) {
//                val events = events.value?.data ?: emptyList()
//                val locations = locations.value?.data ?: emptyList()
//                setValue(results, text, events, locations, it?.dObj ?: emptyList())
//            }

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
                val bookmarks = it.data?.filter { !it.hasFinished }?.take(3) ?: emptyList()
                setHome(result, articles, bookmarks)
            }

            result.addSource(articles) {
                val articles = it.data?.take(4) ?: emptyList()
                val bookmarks =
                    bookmarks.value?.data?.filter { !it.hasFinished }?.take(3) ?: emptyList()
                setHome(result, articles, bookmarks)
            }

            return@switchMap result
        }

    }

    private fun setHome(
        result: MediatorLiveData<Resource<List<Any>>>,
        articles: List<Article>,
        bookmarks: List<Event>
    ) {
        if (bookmarks.isEmpty()) {
            result.value = Resource.success(articles)
        } else {
            result.value = Resource.success(articles + "Bookmarks" + bookmarks)
        }
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

    fun toggleFilter(type: Type) {
        type.isSelected = !type.isSelected
        database.updateTypeIsSelected(type)
    }

    fun clearFilters() {
        val types = types.value?.data ?: emptyList()
        types.forEach {
            if (it.isSelected) {
                it.isSelected = false
                database.updateTypeIsSelected(it)
            }
        }
    }
}