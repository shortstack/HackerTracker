package com.shortstack.hackertracker.network

import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.models.Conferences
import com.shortstack.hackertracker.models.response.Types
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface DatabaseService {

    @GET("conferences/conferences.json")
    fun getConferences(): Call<Conferences>

    @GET("schedule-full.json")
    fun getSchedule(): Call<SyncResponse>

    companion object Factory {

        fun create(database: String): DatabaseService {
            val retrofit = Retrofit.Builder().baseUrl(Constants.API_GITHUB_BASE + database + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            return retrofit.create(DatabaseService::class.java)
        }
    }
}
