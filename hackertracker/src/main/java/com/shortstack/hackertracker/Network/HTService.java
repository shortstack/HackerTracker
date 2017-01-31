package com.shortstack.hackertracker.Network;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Admin on 1/30/2017.
 */

public interface HTService {

    @GET("schedule-full.json")
    Call<SyncResponse> getSync();
}
