package com.shortstack.hackertracker.Network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HTService {

    @GET("schedule-full.json")
    Call<SyncResponse> getSync();
}
