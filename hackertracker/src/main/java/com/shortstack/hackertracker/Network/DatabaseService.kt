package com.shortstack.hackertracker.Network

import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.Model.Types
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface DatabaseService {

    @get:GET("schedule-full.json")
    val getScheduleJob: retrofit2.Call<SyncResponse>

    @get:GET("schedule-full.json")
    val getSchedule: Single<SyncResponse>

    @get:GET("event_type.json")
    val getEventTypes: Single<Types>

    companion object Factory {
        fun create(): DatabaseService {
            val retrofit = Retrofit.Builder().baseUrl(Constants.API_URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            return retrofit.create(DatabaseService::class.java)
        }
    }
}
