package com.shortstack.hackertracker.Event

import com.shortstack.hackertracker.Common.Constants

class SyncResponseEvent(val rowsUpdated: Int = 0, val mode: Int = Constants.SYNC_MODE_AUTO)
