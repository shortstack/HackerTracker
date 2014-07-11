package com.shortstack.hackertracker.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Schedule.SchedulePagerFragment;

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
                SchedulePagerFragment.clearSchedule();
            }
        });

        return dialog;

    }

}
