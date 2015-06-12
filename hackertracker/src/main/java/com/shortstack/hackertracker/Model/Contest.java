package com.shortstack.hackertracker.Model;

import org.parceler.Parcel;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:20 AM
 * Description:
 */

@Parcel
public class Contest extends Default {

    private String forum;

    public void setLink(String forum) {
        this.forum = forum;
    }

    public String getLink() {
        return forum;
    }
}
