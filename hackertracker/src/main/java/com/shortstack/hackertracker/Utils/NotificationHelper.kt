package com.shortstack.hackertracker.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.Trigger
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.network.task.ReminderJob
import com.shortstack.hackertracker.ui.activities.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NotificationHelper @Inject constructor(private val context: Context) {

    @Inject
    lateinit var dispatcher: FirebaseJobDispatcher

    @Inject
    lateinit var database: DatabaseManager

    private val manager = NotificationManagerCompat.from(context)

    init {
        App.application.component.inject(this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(CHANNEL_UPDATES, "Schedule Updates", NotificationManager.IMPORTANCE_HIGH)
                    .apply {
                        description = "Notifications about changes within the schedule"
                        enableLights(true)
                        lightColor = Color.MAGENTA
                    }

            manager.createNotificationChannel(channel)
        }
    }

    private fun getItemNotification(item: Event): Notification {
        val builder = notificationBuilder

        builder.setContentTitle(item.title)
        if (item.location != null) {
            builder.setContentText(String.format(context.getString(R.string.notification_text), item.location))
        } else {
            builder.setContentText(context.getString(R.string.notification_text_blank))
        }

        setItemPendingIntent(builder, item)

        return builder.build()
    }

    fun getUpdatedItemNotification(item: Event): Notification {
        val builder = notificationBuilder

        builder.setContentTitle(item.title)
        builder.setContentText(context.getString(R.string.notification_updated))

        setItemPendingIntent(builder, item)

        return builder.build()
    }

    private val notificationBuilder: NotificationCompat.Builder
        get() {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val color = ContextCompat.getColor(context, R.color.colorPrimary)


            val builder = NotificationCompat.Builder(context, CHANNEL_UPDATES)
            builder.setSound(soundUri)
            builder.setVibrate(longArrayOf(0, 250, 500, 250))
            builder.setLights(Color.MAGENTA, 3000, 1000)

            builder.setSmallIcon(R.drawable.skull)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.color = color
            }
            builder.setAutoCancel(true)

            return builder
        }

    fun scheduleItemNotification(item: Event) {
        val window = item.notificationTime - 1200

        Logger.d("Scheduling event notification. In $window seconds, " + (window / 60) + " mins, " + (window / 3600) + " hrs.")

        if (window <= 0) {
            return
        }

        val bundle = Bundle()
        bundle.putInt(ReminderJob.NOTIFICATION_ID, item.index)

        val job = dispatcher.newJobBuilder()
                .setService(ReminderJob::class.java)
                .setTag(ReminderJob.getTag(item.index))
                .setTrigger(Trigger.executionWindow(window, window))
                .setExtras(bundle)
                .build()

        dispatcher.mustSchedule(job)

    }

    fun notifyUpdates(conference: Conference, newCon: Boolean, rowsUpdated: Int) {
        val builder = notificationBuilder

        if (newCon) {
            builder.setContentTitle(conference.title)
            builder.setContentText("A new conference has been added")
        } else {
            builder.setContentTitle("Schedule Updated")
            builder.setContentText(rowsUpdated.toString() + " events have been updated")
        }

        setItemPendingIntent(builder)

        notify(NOTIFICATION_SCHEDULE_UPDATE, builder.build())
    }

    private fun setItemPendingIntent(builder: NotificationCompat.Builder, item: Event? = null) {
        val intent = Intent(context, MainActivity::class.java)

        if (item != null) {
            val bundle = Bundle()
            bundle.putInt("target", item.index)
            intent.putExtras(bundle)
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentIntent(pendingIntent)
    }


    private fun notify(id: Int, notification: Notification) {
        manager.notify(id, notification)
    }

    fun cancelNotification(id: Int) {
        dispatcher.cancel(ReminderJob.getTag(id))
    }


    fun notify(id: Int) {
        database.findItem(id = id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    manager.notify(id, getItemNotification(it))
                }
    }

    companion object {

        private const val NOTIFICATION_SCHEDULE_UPDATE = -1

        private const val CHANNEL_UPDATES = "updates_channel"

    }
}
