package com.shortstack.hackertracker.Network;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Database.DatabaseController;
import com.shortstack.hackertracker.Event.UpdateListContentsEvent;
import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkController implements Callback<SyncResponse> {

    private final Context mContext;

    private AlertDialog mDialog;

    public NetworkController(Context context) {
        mContext = context;
    }

    public void syncInForeground( Context context ) {
        showSyncingDialog(context);
        sync();
    }

    public void syncInBackground() {
        sync();
    }

    private void sync() {
        Logger.d("Syncing to server.");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API_URL_BASE).addConverterFactory(GsonConverterFactory.create()).build();
        HTService service = retrofit.create(HTService.class);
        service.getSync().enqueue(this);
    }


    private void showSyncingDialog(Context context) {
        mDialog = MaterialAlert.create(context).setTitle(mContext.getString(R.string.sync_title)).setMessage(mContext.getString(R.string.sync_init)).build();
        mDialog.show();
    }

    @Override
    public void onResponse(Call<SyncResponse> call, Response<SyncResponse> response) {
        if( response.isSuccessful() ) {
            updateDatabase(response.body().schedule);
        } else {
            setDialogMessage(mContext.getString(R.string.sync_error));
        }
    }

    private void updateDatabase(final Item[] schedule) {
        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                long time = System.currentTimeMillis();

                DatabaseController database = App.getApplication().getDatabaseController();

                // Remove, only for debugging.
                String[] stringArray = mContext.getResources().getStringArray(R.array.filter_types);
                String[] locationArray = new String[] {"Track 1", "Track 2", "Track 3", "DEFCON 101"};
                int index = 0;


                for (int i = 0; i < schedule.length; i++) {
                    Item scheduleObject = schedule[i];

                    // Remove, only for debugging.
                    scheduleObject.type = stringArray[index];
                    index = ++index % stringArray.length;
                    scheduleObject.location = locationArray[index % locationArray.length];


                    database.updateScheduleItem(scheduleObject);

                    publishProgress(i);
                }

                Logger.d("Total update time: " + (System.currentTimeMillis() - time));
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                setDialogMessage(getProgress(values));


            }

            private String getProgress(Integer... values) {
                return String.format(mContext.getString(R.string.update_progress), values[0], schedule.length );
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                setDialogMessage(mContext.getString(R.string.sync_done));

                App.getApplication().postBusEvent(new UpdateListContentsEvent());
            }
        }.execute();
    }



    @Override
    public void onFailure(Call<SyncResponse> call, Throwable t) {
        setDialogMessage(mContext.getString(R.string.sync_error));
    }

    private void setDialogMessage(String message) {
        if (mDialog != null) {
            mDialog.setMessage(message);
        }
    }
}
