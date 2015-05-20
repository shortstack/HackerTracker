package com.shortstack.hackertracker.Model;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:20 AM
 * Description:
 */
public class Contest extends Default {

    private String forum;

    public void setLink(String forum) {
        this.forum = forum;
    }

    public String getLink() {
        return forum;
    }
}
