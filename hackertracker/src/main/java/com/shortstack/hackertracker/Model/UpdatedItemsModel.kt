package com.shortstack.hackertracker.Model

import java.util.*

class UpdatedItemsModel {

    var id: Int = 0
    var state: Int = 0

    init {
        val random = Random()
        val ids = intArrayOf(7, 178, 164, 126, 228, 208)

        id = ids[random.nextInt(ids.size)]
        state = random.nextInt(1)
    }

    companion object {

        val STATE_NEW = 0
        val STATE_UPDATED = 1
    }
}
