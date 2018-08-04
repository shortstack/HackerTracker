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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Conference
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.network.task.ReminderWorker
import com.shortstack.hackertracker.ui.activities.MainActivity
import java.util.concurrent.TimeUnit
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

            val channel = NotificationChannel(CHANNEL_UPDATES, "Schedule Updates", NotificationManager.IMPORTANCE_DEFAULT)
                    .apply {
                        description = "Notifications about changes within the schedule"
                        enableLights(true)
                        lightColor = Color.MAGENTA
                    }

            manager.createNotificationChannel(channel)
        }
    }

    private fun getStartingSoonNotification(item: DatabaseEvent): Notification {
        val builder = notificationBuilder

        builder.setContentTitle(item.event.title)
        builder.setContentText(String.format(context.getString(R.string.notification_text), item.location.first().name))

        setItemPendingIntent(builder, item.event)

        return builder.build()
    }

    private fun getUpdatedEventNotification(item: DatabaseEvent): Notification {
        val builder = notificationBuilder

        builder.setContentTitle(item.event.title)
        builder.setContentText(context.getString(R.string.notification_updated))

        setItemPendingIntent(builder, item.event)

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

        WorkManager.getInstance()?.cancelAllWorkByTag(ReminderWorker.TAG + item.id)

        val window: Long = (item.notificationTime - 1200).toLong()

        Logger.d("Scheduling event notification. In $window seconds, " + (window / 60) + " mins, " + (window / 3600) + " hrs.")

        if (window <= 0) {
            return
        }

        val data = Data.Builder()
                .putInt(ReminderWorker.NOTIFICATION_ID, item.id).build()

        val request = OneTimeWorkRequest.Builder(ReminderWorker::class.java)
                .setInitialDelay(window, TimeUnit.SECONDS)
                .addTag(ReminderWorker.TAG + item.id)
                .setInputData(data)
                .build()

        WorkManager.getInstance()?.enqueue(request)
    }

    fun notifyUpdates(conference: Conference, newCon: Boolean, rowsUpdated: Int) {
        val builder = notificationBuilder

        when {
            newCon -> {
                builder.setContentTitle(conference.name)
                builder.setContentText("A new conference has been added")
            }
            rowsUpdated > 0 -> {
                builder.setContentTitle("Schedule Updated")
                builder.setContentText(rowsUpdated.toString() + " events have been updated")
            }
            else -> return
        }

        setItemPendingIntent(builder)

        notify(conference.id, builder.build())
    }

    private fun setItemPendingIntent(builder: NotificationCompat.Builder, item: Event? = null) {
        val intent = Intent(context, MainActivity::class.java)

        if (item != null) {
            val bundle = Bundle()
            bundle.putInt("target", item.id)
            intent.putExtras(bundle)
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentIntent(pendingIntent)
    }


    private fun notify(id: Int, notification: Notification) {
        manager.notify(id, notification)
    }

    fun notifyStartingSoon(event: DatabaseEvent) {
        manager.notify(event.event.id, getStartingSoonNotification(event))
    }

    fun updatedBookmarks(updatedBookmarks: List<DatabaseEvent>) {
        updatedBookmarks.forEach {
            notify(it.event.id, getUpdatedEventNotification(it))
        }
    }

    companion object {

        private const val NOTIFICATION_SCHEDULE_UPDATE = -1

        private const val CHANNEL_UPDATES = "updates_channel"

    }
}
