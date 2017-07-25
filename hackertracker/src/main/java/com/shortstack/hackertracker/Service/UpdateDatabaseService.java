package com.shortstack.hackertracker.Service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Database.DatabaseController;
import com.shortstack.hackertracker.Event.SetupDatabaseEvent;

public class UpdateDatabaseService extends IntentService {
    public UpdateDatabaseService() {
        super("DEFCONUpdateDatabaseService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        DatabaseController databaseController = App.application.getDatabaseController();
        App.application.postBusEvent(new SetupDatabaseEvent());
    }
}
