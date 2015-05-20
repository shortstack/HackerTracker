package com.shortstack.hackertracker.Model;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/18/13
 * Time: 10:24 PM
 */
public class Star {

    private String id;
    private String title;
    private String body;
    private String speaker;
    private String begin;
    private String end;
    private String date;
    private String where;
    private String forum;
    private Integer starred;

    public Integer getStarred() {
        return starred;
    }

    public void setStarred(Integer starred) {
        this.starred = starred;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getLink() {
        return forum;
    }

    public void setLink(String forum) {
        this.forum = forum;
    }
}
