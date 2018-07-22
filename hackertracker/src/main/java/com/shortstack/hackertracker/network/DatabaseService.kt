package com.shortstack.hackertracker.network

import com.google.gson.JsonElement
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.models.Conferences
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url


interface DatabaseService {

    @GET(Constants.CONFERENCES_FILE)
    fun getConferences(): Call<Conferences>

    @GET
    fun getSource(@Url url: String): Call<JsonElement>


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
