package com.shortstack.hackertracker.events

import android.os.Handler
import android.os.Looper

import com.squareup.otto.Bus

class BusProvider {

    companion object {
        val bus = MainThreadBus()
    }

    class MainThreadBus : Bus() {

        private val mHandler = Handler(Looper.getMainLooper())

        override fun post(event: Any) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event)
            } else {
                mHandler.post { super@MainThreadBus.post(event) }
            }
        }
    }

}