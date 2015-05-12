package com.shortstack.hackertracker.Model;

/**
 * Created by Whitney Champion on 4/21/14.
 */
public class EventList extends ApiBase {

    private Event[] events;
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Event[] getEvent() {
        return events;
    }

    public void setEvent(Event[] events) {
        this.events = events;
    }

}