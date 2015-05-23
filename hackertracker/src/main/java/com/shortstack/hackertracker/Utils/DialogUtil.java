package com.shortstack.hackertracker.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.shortstack.hackertracker.Activity.HomeActivity;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Schedule.SchedulePagerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by whitneychampion on 7/11/14.
 */
public class DialogUtil {

    public static ProgressDialog getProgressDialog(Context context, String message) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        return progressDialog;

    }

    public static void hideSpinner(ProgressDialog progressDialog) {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    public static AlertDialog apiErrorDialog(final Context context) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_error, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        AlertDialog dialog = builder.create();

        dialog.setView(view, 0, 0, 0, 0);

        Button cancelButton = (Button) view.findViewById(R.id.dismiss);
        final Dialog finalDialog = dialog;
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });

        return dialog;

    }

    public static AlertDialog emptyScheduleDialog(final Context context) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_schedule, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        AlertDialog dialog = builder.create();

        dialog.setView(view, 0, 0, 0, 0);

        Button cancelButton = (Button) view.findViewById(R.id.dismiss);
        final Dialog finalDialog = dialog;
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.dont_show_suggestions);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesUtil.showSuggestions(b);
            }
        });

        return dialog;

    }

    public static AlertDialog clearScheduleDialog(final Context context) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_clear, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        AlertDialog dialog = builder.create();

        dialog.setView(view, 0, 0, 0, 0);

        Button cancelButton = (Button) view.findViewById(R.id.cancel);
        final Dialog finalDialog = dialog;
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });
        Button clearButton = (Button) view.findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
                HomeActivity.clearSchedule(context);
            }
        });

        return dialog;

    }

    public static AlertDialog syncSpeakersDialog(final Context context) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_sync, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        AlertDialog dialog = builder.create();

        dialog.setView(view, 0, 0, 0, 0);

        Button cancelButton = (Button) view.findViewById(R.id.cancel);
        final Dialog finalDialog = dialog;
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });
        Button clearButton = (Button) view.findViewById(R.id.sync);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
                HomeActivity.syncSchedule(context);
            }
        });

        return dialog;

    }

    public static Dialog shareScheduleDialog(final Context context) {

        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle("Options");
        String[] types = {"Save schedule to CSV and Share","Save schedule to CSV only"};
        b.setItems(types, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                switch(which){
                    case 0:
                        // export CSV and share
                        SchedulePagerFragment.backupDatabaseCSV();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out my DEF CON 22 schedule");
                        sendIntent.setType("text/csv");

                        Uri uri = Uri.fromFile(getOutputMediaFile());
                        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

                        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.action_share)));
                        break;
                    case 1:
                        // export CSV
                        SchedulePagerFragment.backupDatabaseCSV();
                        Toast.makeText(context,context.getString(R.string.backup),Toast.LENGTH_SHORT).show();
                }
            }

        });

        Dialog dialog = b.create();
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    public static File getOutputMediaFile()
    {
        // Checks to see if the External Storage SD card is mounted and has write access.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // Creates a Stream folder in the External Storage SD card
            File mediaStorageDir = new File (Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "HackerTracker");

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Stream", "failed to create directory");
                    return null;
                }
            }

            return new File(mediaStorageDir.getPath() + File.separator + "dc22_schedule.csv");
        } else {
            Toast.makeText(HackerTrackerApplication.getAppContext(), "No external storage available!", Toast.LENGTH_LONG).show();
        }

        return null;
    }
}
