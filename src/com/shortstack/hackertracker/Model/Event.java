package com.shortstack.hackertracker.Model;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:20 AM
 * Description:
 */
public class Event {


    private String id;
    private String title;
    private String body;
    private String startTime;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String endTime;
    private String date;
    private String location;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

}
