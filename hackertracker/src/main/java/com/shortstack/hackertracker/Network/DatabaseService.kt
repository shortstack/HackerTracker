package com.shortstack.hackertracker.Network

import com.shortstack.hackertracker.Model.Types
import io.reactivex.Single
import retrofit2.http.GET

interface DatabaseService {

    @get:GET("schedule-full.json")
    val getScheduleJob: retrofit2.Call<SyncResponse>

    @get:GET("schedule-full.json")
    val getSchedule: Single<SyncResponse>

    @get:GET("event_type.json")
    val getEventTypes: Single<Types>
}
