package com.shortstack.hackertracker.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.shortstack.hackertracker.Adapter.SpinnerAdapter;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.DialogUtil;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Whitney Champion on 3/29/14.
 */
public class MapsFragment extends Fragment {

    private static View rootView;
    private Context context;
    private String address;
    private String nickname;
    private RideRequestButton requestButton;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static MapsFragment newInstance(int sectionNumber) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MapsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        } catch (InflateException e) {
            Log.e("MapsFragment",e.getMessage());
        }

        // get context
        context = inflater.getContext();

        // button listeners for defcon map
        Button button_dcmap = (Button) rootView.findViewById(R.id.button_dcmap);
        ImageView image_dcmap = (ImageView) rootView.findViewById(R.id.crop_dcmap);
        button_dcmap.setOnClickListener(new MapButtonOnClickListener(getString(R.string.map_con_name)));
        image_dcmap.setOnClickListener(new MapButtonOnClickListener(getString(R.string.map_con_name)));

        // button listener for hotel map
        Button button_ballysmap = (Button) rootView.findViewById(R.id.button_ballysmap);
        ImageView image_ballysmap = (ImageView) rootView.findViewById(R.id.crop_ballysmap);
        button_ballysmap.setOnClickListener(new MapButtonOnClickListener(getString(R.string.map_hotel_name)));
        image_ballysmap.setOnClickListener(new MapButtonOnClickListener(getString(R.string.map_hotel_name)));

        // set up uber
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId(Constants.UBER_CLIENT_ID)
                .setEnvironment(SessionConfiguration.Environment.PRODUCTION)
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS))
                .build();
        UberSdk.initialize(config);
        requestButton = (RideRequestButton) rootView.findViewById(R.id.uber_request_button);

        // set up spinner
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_uber);
        spinner.setOnItemSelectedListener(new UberOnItemSelectedListener());
        SpinnerAdapter adapter = new SpinnerAdapter(context, R.layout.spinner_item);
        adapter.addAll(Constants.UBER_LOCATIONS);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());

        return rootView;
    }

    public class UberOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            // get address for selected item
            switch (parent.getSelectedItem().toString()) {
                case Constants.UBER_BALLYS:
                    nickname = parent.getSelectedItem().toString();
                    address = Constants.UBER_ADDRESS_BALLYS;
                    break;
                case Constants.UBER_PARIS:
                    nickname = parent.getSelectedItem().toString();
                    address = Constants.UBER_ADDRESS_PARIS;
                    break;
                case Constants.UBER_TUSCANY:
                    nickname = parent.getSelectedItem().toString();
                    address = Constants.UBER_ADDRESS_TUSCANY;
                    break;
                case Constants.UBER_CAESARS:
                    nickname = parent.getSelectedItem().toString();
                    address = Constants.UBER_ADDRESS_CAESARS;
                    break;
                case Constants.UBER_MANDALAY:
                    nickname = parent.getSelectedItem().toString();
                    address = Constants.UBER_ADDRESS_MANDALAY;
                    break;
                case Constants.UBER_PLANET_HOLLYWOOD:
                    nickname = parent.getSelectedItem().toString();
                    address = Constants.UBER_ADDRESS_PLANET_HOLLYWOOD;
                    break;
                case Constants.UBER_BELLAGIO:
                    nickname = parent.getSelectedItem().toString();
                    address = Constants.UBER_ADDRESS_BELLAGIO;
                    break;
                default:
                    nickname = "Paris";
                    address = Constants.UBER_ADDRESS_PARIS;
                    break;
            }

            // set up ride request button
            RideParameters rideParams = new RideParameters.Builder()
                    .setDropoffLocation(0.0,0.0,nickname,address)
                    .build();
            requestButton.setRideParameters(rideParams);

        }

        public void onNothingSelected(AdapterView parent) {
        }
    }

    private class MapButtonOnClickListener implements View.OnClickListener {

        String filename;

        MapButtonOnClickListener(String filename) {
            this.filename = filename;
        }

        @Override
        public void onClick(View v) {

            // copy map to sdcard
            copyAssets(context, filename);


            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename);
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file),"application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, getString(R.string.open_file));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context,getString(R.string.no_pdf_reader),Toast.LENGTH_SHORT).show();
            }

        }

    }

    // copies map PDFs to SD card if available
    private void copyAssets(Context context, String filename) {

        AssetManager assetManager = context.getAssets();

        InputStream in;
        OutputStream out;

        try {
            in = assetManager.open(filename);
            out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    // actually copies file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

}