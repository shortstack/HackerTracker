package com.shortstack.hackertracker.Model;

/**
 * Created by Whitney Champion on 4/21/14.
 */
public class OfficialList extends ApiBase {

    private String updateDate;
    private String updateTime;
    private Default[] schedule;

    public Default[] getAll() {
        return schedule;
    }

    public void setAll(Default[] schedule) {
        this.schedule = schedule;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

}