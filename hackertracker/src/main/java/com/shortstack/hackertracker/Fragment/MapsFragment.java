package com.shortstack.hackertracker.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.DialogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Whitney Champion on 3/29/14.
 */
public class MapsFragment extends Fragment {

    private static View rootView;
    private Context context;
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
        button_dcmap.setOnClickListener(new MapButtonOnClickListener(getString(R.string.map_con_name)));

        // button listener for hotel map
        Button button_ballysmap = (Button) rootView.findViewById(R.id.button_ballysmap);
        button_ballysmap.setOnClickListener(new MapButtonOnClickListener(getString(R.string.map_hotel_name)));

        return rootView;
    }

    private class MapOnClickListener implements View.OnClickListener {

        int clicks = 0;

        @Override
        public void onClick(View v) {
            clicks += 1;
            if (clicks == 1) {
                Toast.makeText(context, getString(R.string.message04), Toast.LENGTH_SHORT).show();
            } else if (clicks == 2) {
                Toast.makeText(context, getString(R.string.message05), Toast.LENGTH_SHORT).show();
            } else if (clicks == 3) {
                DialogUtil.darknetDialog(context, getResources().getString(R.string.code01)).show();
            }
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