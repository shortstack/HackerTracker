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
    private String who;
    private String description;
    private String begin;
    private String end;
    private String where;
    private String link;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String who) {
        this.who = who;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return who;
    }

    public String getBegin() {
        return begin;
    }

    public String getEnd() {
        return end;
    }

    public int getDate() {
        return date;
    }

    public String getWhere() {
        return where;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
