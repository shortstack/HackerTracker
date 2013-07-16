package com.shortstack.hackertracker.Model;

import android.graphics.drawable.Drawable;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/15/13
 * Time: 11:17 PM
 */
public class Vendor {

    private String id;
    private String title;
    private String body;
    private String logo;
    private String website;

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }


}
