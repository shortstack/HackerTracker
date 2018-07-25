package com.shortstack.hackertracker.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(foreignKeys = [(ForeignKey(entity = (Conference::class), parentColumns = [("code")], childColumns = [("conference")], onDelete = ForeignKey.CASCADE))])
data class Type(
        @PrimaryKey
        val id: Int,
        val name: String,
        val color: String,
        val conference: String,
        var isSelected: Boolean) {

    companion object {
        private const val BOOKMARK_ID = -1
    }

    val isBookmark
        get() = id == BOOKMARK_ID

}