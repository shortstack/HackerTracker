package com.advice.schedule.models.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class FirebaseTagType(
    val conference: String = "",
    val conference_id: Long = -1,

    val id: Long = -1,
    val category: String = "",
    @field:JvmField
    val is_browsable: Boolean = true,
    @field:JvmField
    val is_single_valued: Boolean = false,
    val label: String = "",
    val sort_order: Int = 0,

    val tags: List<FirebaseTag> = emptyList()
)

@Parcelize
data class FirebaseTag(
    val id: Long = -1,
    val label: String = "",
    val description: String = "",
    val color_background: String? = null,
    val color_foreground: String? = null,
    val sort_order: Int = 0,

    // todo: move to client model
    var isSelected: Boolean = false
) : Parcelable {

    companion object {
        val bookmark = FirebaseTag(-1, "Bookmark", color_background = "#FFFFFF", color_foreground = "#000000")
    }

    // todo: remove default
    val color: String
        get() = color_background ?: "#FF0000"

    val isBookmark: Boolean
        get() = this == bookmark
}