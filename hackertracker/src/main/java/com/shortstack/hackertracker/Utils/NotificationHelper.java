package com.shortstack.hackertracker.Utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.R;

public class NotificationHelper {

    private Context mContext;

    private AlarmManager alarmManager;
    private BroadcastReceiver receiver;
    private PendingIntent pendingIntent;

    public NotificationHelper( Context context ) {
        super();
        mContext = context;
        RegisterAlarmBroadcast();
    }

    private void RegisterAlarmBroadcast() {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }

        };

        // register the alarm broadcast
        mContext.registerReceiver(receiver, new IntentFilter(mContext.getPackageName()));
        pendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(mContext.getPackageName()), 0);
        alarmManager = (AlarmManager) (mContext.getSystemService(Context.ALARM_SERVICE));

    }


    public Notification getItemNotification(Item content) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int color = mContext.getResources().getColor(R.color.colorPrimary);

        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentTitle(content.getTitle());
        builder.setContentText(String.format(mContext.getString(R.string.notification_text), content.getLocation()));
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setSound(soundUri);
        builder.setVibrate(new long[] { 0, 250, 500, 250 });
        builder.setLights(Color.MAGENTA, 3000, 1000);

        builder.setSmallIcon(R.drawable.skull);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder.setColor(color);
        }
        builder.setAutoCancel(true);

        return builder.build();
    }

    public void scheduleItemNotification( Item item ) {
        scheduleNotification(getItemNotification(item), item.getNotificationTimeInMillis(), item.getId() );
    }

    public void scheduleNotification(Notification notification, long when, int id) {

        Intent notificationIntent = new Intent(mContext, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, id);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
    }

    private void UnregisterAlarmBroadcast() {
        alarmManager.cancel(pendingIntent);
        mContext.unregisterReceiver(receiver);
    }

    public void cancelNotification(int id) {

        Intent notificationIntent = new Intent(mContext, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, id);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();

        alarmManager.cancel(pendingIntent);
    }

}
