package com.shortstack.hackertracker.Model;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:20 AM
 * Description:
 */
public class Speaker {


    private String id;
    private String title;
    private String body;
    private String speaker;
    private String startTime;
    private String endTime;
    private String date;
    private String location;
    private Boolean demo;
    private Boolean info;
    private Boolean exploit;
    private Boolean tool;
    private Integer starred;

    public Integer getStarred() {
        return starred;
    }

    public void setStarred(Integer starred) {
        this.starred = starred;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
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

    public void setDemo(Boolean demo) {
        this.demo = demo;
    }

    public void setTool(Boolean tool) {
        this.tool = tool;
    }

    public void setInfo(Boolean info) {
        this.info = info;
    }

    public void setExploit(Boolean exploit) {
        this.exploit = exploit;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getSpeaker() {
        return speaker;
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

    public Boolean getDemo() {
        return demo;
    }

    public Boolean getTool() {
        return tool;
    }

    public Boolean getInfo() {
        return info;
    }

    public Boolean getExploit() {
        return exploit;
    }



}
