package com.shortstack.hackertracker

interface BasePresenter {

    fun <T> takeView(view : T)

    fun dropView()

}