package com.shortstack.hackertracker.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(foreignKeys = [(ForeignKey(entity = (Conference::class), parentColumns = [("code")], childColumns = [("conference")], onDelete = ForeignKey.CASCADE))])
data class Speaker(
        @PrimaryKey
        val id: Int,
        val name: String,
        val title: String?,
        val description: String,
        val link: String?,
        val twitter: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        val conference: String
) : Parcelable

