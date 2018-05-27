package com.shortstack.hackertracker.network

import com.shortstack.hackertracker.models.response.Types

class FullResponse(val syncResponse: SyncResponse, val types: Types)