package com.shortstack.hackertracker.Model;

import android.content.Context;

import com.orhanobut.logger.Logger;

public class Information {
    String title;
    String description;


    public Information(Context context, int res) {
        String[] array = context.getResources().getStringArray(res);
        if( array.length != 2 ) {
            Logger.e("Information array is not set up correct, size is not 2. " + (array.length));
        }
        title = array[0];
        description = array[1];
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
