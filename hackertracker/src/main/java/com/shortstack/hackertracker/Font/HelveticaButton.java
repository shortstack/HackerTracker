package com.shortstack.hackertracker.Font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Whitney Champion on 6/15/15.
 */
public class HelveticaButton extends Button {

    public HelveticaButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HelveticaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HelveticaButton(Context context) {
        super(context);
        init();
    }

    public void init() {

        Typeface tf = Typeface.createFromAsset
                (getContext().getAssets(), "font/HelveticaNeue-Thin.ttf");
        setTypeface(tf ,1);

    }
}

