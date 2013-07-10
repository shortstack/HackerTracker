package com.shortstack.hackertracker.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.shortstack.hackertracker.R;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/29/12
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class maps extends HackerTracker {

    private static Context context;

    private void copyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                out = new FileOutputStream("/sdcard/" + filename);
                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch(Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);

        // copy pdf files to sdcard

        maps.context = getApplicationContext();
        copyAssets(context);

        // button listener for defcon map

        Button button_dcmap = (Button)findViewById(R.id.button_dcmap);
        button_dcmap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AlertDialog.Builder pdfConfirm = new AlertDialog.Builder(maps.this);
                pdfConfirm.setTitle("Map Requires PDF Reader");
                pdfConfirm.setMessage("Do you have a PDF reader installed?");
                pdfConfirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        File file = new File("/sdcard/map_defcon.pdf");

                        PackageManager packageManager = getPackageManager();
                        Intent testIntent = new Intent(Intent.ACTION_VIEW);
                        testIntent.setType("application/pdf");
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(file);
                        intent.setDataAndType(uri, "application/pdf");

                        startActivity(intent);
                    }
                });
                pdfConfirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                pdfConfirm.show();
            }
        });

        // button listener for rio map

        Button button_riomap = (Button)findViewById(R.id.button_riomap);
        button_riomap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder pdfConfirm = new AlertDialog.Builder(maps.this);
                pdfConfirm.setTitle("Map Requires PDF Reader");
                pdfConfirm.setMessage("Do you have a PDF reader installed?");
                pdfConfirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        File file = new File("/sdcard/map_rio.pdf");

                        PackageManager packageManager = getPackageManager();
                        Intent testIntent = new Intent(Intent.ACTION_VIEW);
                        testIntent.setType("application/pdf");
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(file);
                        intent.setDataAndType(uri, "application/pdf");

                        startActivity(intent);
                    }
                });
                pdfConfirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                pdfConfirm.show();
            }
        });

    }

    public static Context getAppContext() {
        return maps.context;
    }
}
