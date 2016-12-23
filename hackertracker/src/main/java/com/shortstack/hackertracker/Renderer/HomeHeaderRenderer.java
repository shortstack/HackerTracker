package com.shortstack.hackertracker.Renderer;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeHeaderRenderer extends Renderer<Void> {

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.header_home, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void render(List<Object> payloads) {
        // Do nothing.


        String string = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        String[] source = new String[] { "a1ae1f441763ca44"/*, "9774d56d682e549c" */};


        int setCount = 70;

        String temp = "";

        for (int i = 0; i < setCount; i++) {
            char aChar = getChar(i);
            temp = temp.concat( "[" + i + "] = " + aChar);
        }

        Logger.d(temp);

        for (String s : source) {

            int size = 16;
            int output = 0;


//            for (int i = 0; i < output.length; i++) {
//                output[i] = 97;
//            }


            for(int i = 0; i < s.length(); i++ ){
                output += (int)s.charAt(i);
            }

//            Logger.d("Value: " + ((int)output));
//
//            output = (output / 4 % 4) * (setCount / 4);
//            Logger.d("After floor: " + ((int)output));
//
//            Logger.d("Converted " + ( output ) + " to " + ( getChar(output)));
//
//            Logger.d("String: " + String.valueOf(output) );
//            Logger.d("Full String: " + getFullString(output));

        }

    }

    private char getChar( int value ) {
        if( value < 26 ) {
            return (char) (value + 97);
        }

        value -= 26;
        if( value < 26 ) {
            return (char) (value + 65);
        }

        value -= 26;
        if( value < 10 ) {
            return (char) (value + 48);
        }
        value -= 10;

        return (char) (value + 35);
    }

    private String getFullString( int value ) {
        String result = "";
        for (int i = 0; i < 16; i++) {
            int ex = (int) Math.pow(i + 36, 5);
            ex = ex % 23;

            ex--;

            result = result.concat(String.valueOf(getChar(ex)));
            if( i % 4 == 0 )
                result = result.concat("-");
        }

        return result;
    }

    @OnClick(R.id.logo)
    public void onSkullClick() {
        // TODO Implement skull animation.
    }
}
