package com.shortstack.hackertracker.Network

import retrofit2.Call
import retrofit2.http.GET

interface HTService {

    @get:GET("schedule-full.json")
    val sync: Call<SyncResponse>
}
