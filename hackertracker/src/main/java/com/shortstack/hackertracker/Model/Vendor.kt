package com.shortstack.hackertracker.Model

import android.text.TextUtils
import java.io.Serializable

class Vendors {

    lateinit var vendors: Array<Vendor>

    class Vendor : Serializable {

        lateinit var title: String
        lateinit var description: String
        lateinit var link: String
        var partner: Int = 0

        val isPartner: Boolean
            get() = partner == 1

        fun hasLink(): Boolean {
            return !TextUtils.isEmpty(link)
        }
    }
}
