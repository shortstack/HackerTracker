package com.shortstack.hackertracker.models.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vendor(
        private val id: Int,
        val name: String,
        private val description: String?,
        val link: String?,
        val partner: Boolean
) : Parcelable {

    val summary: String
        get() {
            if (description.isNullOrBlank())
                return "Nothing to say."
            return description.replace("\\n", "\n")
        }
}

