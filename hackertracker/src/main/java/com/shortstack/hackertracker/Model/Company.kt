package com.shortstack.hackertracker.Model

import android.text.TextUtils

import java.io.Serializable

class Company : Serializable {

    var id: Int = 0
    var title: String? = null
    var description: String? = null
    var link: String? = null
    var partner: Int = 0

    val isPartner: Boolean
        get() = partner == 1

    fun hasLink(): Boolean {
        return !TextUtils.isEmpty(link)
    }
}
