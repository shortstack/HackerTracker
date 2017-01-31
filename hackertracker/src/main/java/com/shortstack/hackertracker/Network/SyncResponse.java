package com.shortstack.hackertracker.Network;

import com.google.gson.annotations.SerializedName;

public class SyncResponse {

    @SerializedName("updateTime")
    public String time;
    @SerializedName("updateDate")
    public String date;

    public ScheduleObject[] schedule;

    @Override
    public String toString() {
        return time + ", " + date + ", " + schedule.length;
    }
}
