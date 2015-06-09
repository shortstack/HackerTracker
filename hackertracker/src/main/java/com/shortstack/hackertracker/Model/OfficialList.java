package com.shortstack.hackertracker.Model;

/**
 * Created by Whitney Champion on 4/21/14.
 */
public class OfficialList extends ApiBase {

    private Default[] schedule;

    public Default[] getAll() {
        return schedule;
    }

    public void setAll(Default[] schedule) {
        this.schedule = schedule;
    }

}