package com.shortstack.hackertracker;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orhanobut.logger.Logger;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Logger.d("onMessageReceived");
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Logger.d("onDeletedMessages");
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Logger.d("onMessageSent");
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
        Logger.d("onSendError");
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Logger.d("onNewToken");
    }
}
