package com.shortstack.hackertracker.network

import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.models.Conferences
import com.shortstack.hackertracker.models.Events
import com.shortstack.hackertracker.models.FAQs
import com.shortstack.hackertracker.models.Locations
import com.shortstack.hackertracker.models.response.Speakers
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface DatabaseService {

    @GET(Constants.CONFERENCES_FILE)
    fun getConferences(): Call<Conferences>

    @GET(Constants.TYPES_FILE)
    fun getTypes(): Call<Types>

    @GET(Constants.LOCATIONS_FILE)
    fun getLocations(): Call<Locations>

    @GET(Constants.SPEAKERS_FILE)
    fun getSpeakers(): Call<Speakers>

    @GET(Constants.SCHEDULE_FILE)
    fun getSchedule(): Call<Events>

    @GET(Constants.VENDORS_FILE)
    fun getVendors(): Call<Vendors>

    @GET(Constants.FAQ_FILE)
    fun getFAQs(): Call<FAQs>


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
