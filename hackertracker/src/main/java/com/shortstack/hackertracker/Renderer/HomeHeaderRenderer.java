package com.shortstack.hackertracker.Renderer;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Activity.FAQActivity;
import com.shortstack.hackertracker.Activity.MapsActivity;
import com.shortstack.hackertracker.Activity.SettingsActivity;
import com.shortstack.hackertracker.Activity.VendorsActivity;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Fragment.InformationBottomSheetDialogFragment;
import com.shortstack.hackertracker.Fragment.InformationFragment;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.UberView;

import java.util.List;

import butterknife.ButterKnife;

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

    private void showInformationAlert(int res) {
        View view = LayoutInflater.from(getContext()).inflate(res, null);
        MaterialAlert.create(getContext()).setView(view).show();
    }

    private void showTextAlert( int title, int message ) {
        MaterialAlert.create(getContext()).setTitle(title).setMessage(message).show();
    }

    private void startInformationActivity( int res ) {
        Intent intent = new Intent(getContext(), InformationFragment.class);
        intent.putExtra("res", res);
        getContext().startActivity(intent);
    }

//    @OnClick(R.id.badge)
    public void onBadgeClick() {
        showInformationAlert(R.layout.alert_badges);
    }

//    @OnClick(R.id.wifi)
    public void onWifiClick() {
        showInformationAlert(R.layout.alert_wifi);
    }

//    @OnClick(R.id.map)
    public void onMapClick() {
        //startInformationActivity(R.layout.fragment_maps);
        Intent intent = new Intent(getContext(), MapsActivity.class);
        //intent.putExtra("res", res);
        getContext().startActivity(intent);
    }

    //@OnClick(R.id.vendors)
    public void onVendorsClick() {
        Intent intent = new Intent(getContext(), VendorsActivity.class);
        getContext().startActivity(intent);
    }

    //@OnClick(R.id.faqs)
    public void onFAQClick() {
        Intent intent = new Intent(getContext(), FAQActivity.class);
        getContext().startActivity(intent);
    }

    //@OnClick(R.id.workshop)
    public void onWorkshopClick() {
        showTextAlert(R.string.workshop_info_title, R.string.workshop_info_text);
    }

    //@OnClick(R.id.radio)
    public void onRadioClick() {
        showTextAlert(R.string.radio_title, R.string.radio_text);
    }

//    @OnClick(R.id.settings)
    public void onSettingsClick() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        getContext().startActivity(intent);
    }

//    @OnClick(R.id.uber)
    public void onUberClick() {
        MaterialAlert.create(getContext()).setTitle(R.string.uber).setView( new UberView(getContext())).show();
    }

//    @OnClick(R.id.information)
    public void onINformationClick() {
//        Intent intent = new Intent(getContext(), InformationActivity.class);
//        getContext().startActivity(intent);


        InformationBottomSheetDialogFragment badges = InformationBottomSheetDialogFragment.newInstance("Badges", getContext().getString(R.string.badge_text));
        badges.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), badges.getTag());

    }
}
