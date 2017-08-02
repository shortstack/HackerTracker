package com.shortstack.hackertracker.Model

import android.content.Context

import com.orhanobut.logger.Logger

class Information(context: Context, res: Int) {

    var title: String
        internal set
    var description: String
        internal set

    init {
        val array = context.resources.getStringArray(res)
        if (array.size != 2) {
            Logger.e("Information array is not set up correct, size is not 2. " + array.size)
        }
        title = array[0]
        description = array[1]
    }
}
