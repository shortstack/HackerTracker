package com.shortstack.hackertracker.Model;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

public class Speaker {

    private String title;
    @SerializedName("who")
    private String name;
    private int id;

    private String lastUpdate;
    private String media;
    private String bio;


    public static Speaker CursorToItem(Gson gson, Cursor cursor) {
        JSONObject object = new JSONObject();

        int totalColumn = cursor.getColumnCount();

        for (int i = 0; i < totalColumn; i++) {
            try {
                object.put(cursor.getColumnName(i), cursor.getString(i));
            } catch (Exception e) {
                Logger.e(e, "Failed to convert Cursor into JSONObject.");
            }
        }

        return gson.fromJson(object.toString(), Speaker.class);
    }

    public String getName() {
        return name;
    }

}
