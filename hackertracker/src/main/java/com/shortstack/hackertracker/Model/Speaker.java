package com.shortstack.hackertracker.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Speaker implements Serializable {

    private String title;
    @SerializedName("who")
    private String name;
    private int id;

    private String lastUpdate;
    private String media;
    private String bio;

    public String getName() {
        return name;
    }

}
