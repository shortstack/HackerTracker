package com.advice.schedule.ui.information.faq

import androidx.lifecycle.*
import com.advice.schedule.Response
import com.advice.schedule.dObj
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.firebase.FirebaseFAQ
import com.advice.schedule.models.local.FAQAnswer
import com.advice.schedule.models.local.FAQQuestion
import com.advice.schedule.toFAQ
import org.koin.core.KoinComponent
import org.koin.core.inject

class FAQViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val source: LiveData<List<FirebaseFAQ>>

    private val faq = MediatorLiveData<Response<List<Any>>>()
    private val searchQuery = MutableLiveData<String?>()

    init {
        source = Transformations.switchMap(database.conference) {
            val result = MutableLiveData<List<FirebaseFAQ>>()

            if (it != null) {
                faq.addSource(database.getFAQ(it)) {
                    result.value = it
                }
            }

            return@switchMap result
        }

        faq.addSource(source) {
            faq.value = getList(it, searchQuery.value)
        }

        faq.addSource(searchQuery) { query ->
            faq.value = getList(source.value ?: emptyList(), query)
        }
    }

    private fun getList(list: List<FirebaseFAQ>, query: String?): Response<List<Any>> {
        val data = list
            .filter { query == null || (query in it.question || query in it.answer) }
            .mapNotNull {
                it.toFAQ(query != null && query.isNotBlank() && query !in it.question && query in it.answer)?.toList()
            }
            .flatten()

        return Response.Success(data)
    }

    fun setSearchQuery(query: String?) {
        searchQuery.value = query
    }

    fun toggle(question: FAQQuestion) {
        val list = (faq.value?.dObj as? List<Any>)?.toMutableList() ?: return

        val answer = list.find { it is FAQAnswer && it.id == question.id } as? FAQAnswer
        val question = list.find { it is FAQQuestion && it.id == question.id } as? FAQQuestion

        if (question != null && answer != null) {
            list[list.indexOf(question)] = question.copy(isExpanded = !question.isExpanded)
            list[list.indexOf(answer)] = answer.copy(isExpanded = !answer.isExpanded)
        }

        faq.value = Response.Success(list)
    }

    fun getFAQ(): LiveData<Response<List<Any>>> = faq
}