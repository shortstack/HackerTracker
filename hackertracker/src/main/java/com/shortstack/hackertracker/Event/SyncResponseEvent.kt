package com.shortstack.hackertracker.event

import com.shortstack.hackertracker.Constants

class SyncResponseEvent(val rowsUpdated: Int = 0, val mode: Int = Constants.SYNC_MODE_AUTO)
