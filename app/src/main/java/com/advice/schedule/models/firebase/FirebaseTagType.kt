package com.advice.schedule.models.firebase

data class FirebaseTagType(
    val conference: String = "",
    val conference_id: Long = -1,

    val id: Long = -1,
    val category: String = "",
    val is_browsable: Boolean = true,
    val is_single_valued: Boolean = false,
    val label: String = "",

    val tags: List<FirebaseTag> = emptyList()
)

data class FirebaseTag(
    val id: Long = -1,
    val label: String = "",
    val description: String = "",
    val color_background: String? = null,
    val color_foreground: String? = null,

    // todo: move to client model
    var isSelected: Boolean = false
) {
    // todo: remove default
    val color: String
        get() = color_background ?: "#FF0000"
}