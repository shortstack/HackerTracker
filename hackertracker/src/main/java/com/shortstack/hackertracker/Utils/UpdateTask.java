package com.shortstack.hackertracker.Utils;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.shortstack.hackertracker.Activity.HomeActivity;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.Model.OfficialList;
import com.shortstack.hackertracker.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Whitney Champion on 6/15/15.
 */
public class UpdateTask extends AsyncTask<String, Void, Boolean> {

    public UpdateTask(OfficialList schedule, String update, Context context) {
        this.schedule = schedule;
        this.update = update;
        this.context = context;
        dialog = DialogUtil.getProgressDialog(context, context.getResources().getString(R.string.syncing));
    }

    private ProgressDialog dialog;
    private OfficialList schedule;
    private Context context;
    private String update;

    protected void onPreExecute() {
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if (success) {
            Toast.makeText(context, R.string.schedule_finished, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,R.string.no_updates,Toast.LENGTH_SHORT).show();
        }
    }

    protected Boolean doInBackground(final String... args) {

            ArrayList<Default> officialArray = new ArrayList(Arrays.asList(schedule.getAll()));

            if (officialArray.size()!=0) {
                HomeActivity.syncDatabase(officialArray, context);
                dialog.dismiss();

                // update last updated device date to last updated online date
                SharedPreferencesUtil.saveLastUpdated(update);

                return true;
            } else {
                dialog.dismiss();
                // no results found, don't sync
                return false;
            }


    }
}


