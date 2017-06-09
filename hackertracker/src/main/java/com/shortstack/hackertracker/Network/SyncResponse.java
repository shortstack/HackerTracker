package com.shortstack.hackertracker.Network;

import com.google.gson.annotations.SerializedName;
import com.shortstack.hackertracker.Model.Item;

public class SyncResponse {

    @SerializedName("updateTime")
    public String time;
    @SerializedName("updateDate")
    public String date;

    public Item[] schedule;

    @Override
    public String toString() {
        return time + ", " + date + ", " + schedule.length;
    }
}
