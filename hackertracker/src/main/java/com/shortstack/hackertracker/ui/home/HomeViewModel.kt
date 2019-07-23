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

    private val recent = database.getRecent()
    private val articles = database.getArticles()
    private val faq = database.getFAQ()

    init {
        results = Transformations.switchMap(database.conference) {
            val results = MediatorLiveData<List<Any>>()


            results.addSource(recent) {
                val articles = articles.value ?: emptyList()
                setValue(results, articles, it)
            }

            results.addSource(articles) {
                val recent = recent.value ?: emptyList()
                setValue(results, it, recent)
            }

            results.addSource(faq) {
                Logger.d("Got FAQ.")
            }


            return@switchMap results
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