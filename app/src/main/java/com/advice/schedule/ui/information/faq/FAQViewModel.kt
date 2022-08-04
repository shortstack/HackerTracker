package com.advice.schedule.ui.information.faq

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.advice.schedule.Response
import com.advice.schedule.dObj
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.local.FAQAnswer
import com.advice.schedule.models.local.FAQQuestion
import org.koin.core.KoinComponent
import org.koin.core.inject

class FAQViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    private val faq = MediatorLiveData<Response<List<Any>>>()

    init {
        faq.addSource(database.conference) {
            if (it == null) {
                faq.value = Response.Init
            } else {
                faq.value = Response.Loading
                faq.addSource(database.getFAQ(it)) {
                    faq.value = Response.Success(it)
                }
            }
        }
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