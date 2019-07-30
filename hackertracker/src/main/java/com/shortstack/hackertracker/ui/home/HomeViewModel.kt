package com.shortstack.hackertracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Article
import com.shortstack.hackertracker.models.local.Event
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val results: LiveData<List<Any>>

    private val bookmarks = database.getBookmarks()
    private val recent = database.getRecent()
    private val articles = database.getArticles()

    init {
        results = Transformations.switchMap(database.conference) {
            val results = MediatorLiveData<List<Any>>()

            results.addSource(bookmarks) {
                setValue(results, bookmarks = it)
            }

            results.addSource(recent) {
                setValue(results, recent = it)
            }

            results.addSource(articles) {
                setValue(results, articles = it)
            }

            return@switchMap results
        }
    }

    private fun setValue(results: MediatorLiveData<List<Any>>,
                         articles: List<Article> = this.articles.value ?: emptyList(),
                         recent: List<Event> = this.recent.value ?: emptyList(),
                         bookmarks: List<Event> = this.bookmarks.value ?: emptyList()) {
        val list = ArrayList<Any>()

        if (bookmarks.isNotEmpty()) {
            list.add("Saved Events")
            list.addAll(bookmarks)
        }

        if (articles.isNotEmpty()) {
            list.add("Announcements")
            list.addAll(articles)
        }

        if (recent.isNotEmpty()) {
            list.add("Recent Updates")
            list.addAll(recent)
        }
    }

    private fun setValue(results: MediatorLiveData<List<Any>>, articles: List<Article>, recent: List<Event>) {
        val list = ArrayList<Any>()

        list.add("Announcements")
        list.addAll(articles)
        list.add("Recent Updates")
        list.addAll(recent)

        results.postValue(list)
    }
}