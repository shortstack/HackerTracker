package com.shortstack.hackertracker.Font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Whitney Champion on 6/15/15.
 */
public class HelveticaTextView extends TextView {

    public HelveticaTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HelveticaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HelveticaTextView(Context context) {
        super(context);
        init();
    }

    public void init() {

        Typeface tf = Typeface.createFromAsset
                (getContext().getAssets(), "font/HelveticaNeue-Thin.ttf");
        setTypeface(tf ,1);

    }
}
