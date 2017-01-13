package com.shortstack.hackertracker.Model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/15/13
 * Time: 11:17 PM
 */
public class Company implements Serializable {

    private int id;
    private String title;
    private String description;
    private String link;
    private int partner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean hasLink() {
        return !TextUtils.isEmpty(link);
    }

    public void setPartner(int partner) {
        this.partner = partner;
    }

    public boolean isPartner() {
        return partner == 1;
    }
}
