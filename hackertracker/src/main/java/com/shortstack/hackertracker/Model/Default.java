package com.shortstack.hackertracker.Model;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:20 AM
 * Description:
 */
public class Default {

    private int id;
    private int type;
    private int date;
    private String title;
    private String name;
    private String body;
    private String startTime;
    private String endTime;
    private String location;
    private String forum;
    private Integer starred;
    private String image;
    private Integer is_new;
    private Integer demo;
    private Integer tool;
    private Integer exploit;

    public Integer getStarred() {
        return starred;
    }

    public void setStarred(Integer starred) {
        this.starred = starred;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getName() {
        return name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public Integer isNew() {
        return is_new;
    }

    public void setIsNew(Integer is_new) {
        this.is_new = is_new;
    }

    public Integer getDemo() {
        return demo;
    }

    public void setDemo(Integer demo) {
        this.demo = demo;
    }

    public Integer getTool() {
        return tool;
    }

    public void setTool(Integer tool) {
        this.tool = tool;
    }

    public Integer getExploit() {
        return exploit;
    }

    public void setExploit(Integer exploit) {
        this.exploit = exploit;
    }

}
