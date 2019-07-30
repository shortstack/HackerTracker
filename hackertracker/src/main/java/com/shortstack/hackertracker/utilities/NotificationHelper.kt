package com.shortstack.hackertracker.utilities

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
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.activities.MainActivity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class NotificationHelper(private val context: Context) : KoinComponent {

    private val dispatcher: FirebaseJobDispatcher by inject()

    private val database: DatabaseManager by inject()

    private val manager = NotificationManagerCompat.from(context)

    init {
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

    private fun getStartingSoonNotification(item: Event): Notification {
        val builder = notificationBuilder

        builder.setContentTitle(item.title)
        builder.setContentText(String.format(context.getString(R.string.notification_text), item.location.name))

        setItemPendingIntent(builder, item)

        return builder.build()
    }

    private fun getUpdatedEventNotification(item: Event): Notification {
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

    fun notifyStartingSoon(event: Event) {
        manager.notify(event.id, getStartingSoonNotification(event))
    }

    fun updatedBookmarks(updatedBookmarks: List<Event>) {
        updatedBookmarks.forEach {
            notify(it.id, getUpdatedEventNotification(it))
        }
    }

    companion object {
        private const val CHANNEL_UPDATES = "updates_channel"

    }
}
