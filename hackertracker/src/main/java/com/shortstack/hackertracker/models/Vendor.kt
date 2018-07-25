package com.shortstack.hackertracker.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(foreignKeys = [(ForeignKey(entity = (Conference::class), parentColumns = [("code")], childColumns = [("conference")], onDelete = ForeignKey.CASCADE))])
data class Vendor(
        @PrimaryKey(autoGenerate = true)
        val index: Int,
        val title: String,
        val description: String,
        val link: String,
        val partner: Int,
        val conference: String
) : Parcelable

