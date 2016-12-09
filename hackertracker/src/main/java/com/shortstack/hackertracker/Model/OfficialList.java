package com.shortstack.hackertracker.Model;

/**
 * Created by Whitney Champion on 4/21/14.
 */
public class OfficialList extends ApiBase {

    private String updateDate;
    private String updateTime;
    private Item[] schedule;

    public Item[] getAll() {
        return schedule;
    }

    public void setAll(Item[] schedule) {
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