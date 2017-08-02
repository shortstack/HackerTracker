package com.shortstack.hackertracker.Utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import com.firebase.jobdispatcher.Trigger
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Activity.TabHomeActivity
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Task.ReminderJob

class NotificationHelper(private val mContext: Context) {


    fun getItemNotification(item: Item): Notification {
        val builder = notificationBuilder

        builder.setContentTitle(item.title)
        if (item.location != null) {
            builder.setContentText(String.format(mContext.getString(R.string.notification_text), item.location))
        } else {
            builder.setContentText(mContext.getString(R.string.notification_text_blank))
        }

        setItemPendingIntent(builder, item)

        return builder.build()
    }

    fun getUpdatedItemNotification(item: Item): Notification {
        val builder = notificationBuilder

        builder.setContentTitle(item.title)
        builder.setContentText(mContext.getString(R.string.notification_updated))

        setItemPendingIntent(builder, item)

        return builder.build()
    }

    private val notificationBuilder: Notification.Builder
        get() {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val color = mContext.resources.getColor(R.color.colorPrimary)

            val builder = Notification.Builder(mContext)
            builder.setPriority(Notification.PRIORITY_MAX)
            builder.setSound(soundUri)
            builder.setVibrate(longArrayOf(0, 250, 500, 250))
            builder.setLights(Color.MAGENTA, 3000, 1000)

            builder.setSmallIcon(R.drawable.skull)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                builder.setColor(color)
            }
            builder.setAutoCancel(true)

            return builder
        }

    fun scheduleItemNotification(item: Item) {
        val dispatcher = App.application.dispatcher

        val window = item.notificationTime - 1200

        Logger.d("Scheduling item notification. In $window seconds, " + (window / 60) + " mins, " + (window/3600) + " hrs."  )

        if( window <= 0 ) {
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

    fun scheduleUpdateNotification(rowsUpdated: Int) {
        val builder = notificationBuilder
        builder.setContentTitle("Schedule Updated")
        builder.setContentText(rowsUpdated.toString() + " items have been updated.")

        setItemPendingIntent(builder)

        postNotification(builder.build(), NOTIFICATION_SCHEDULE_UPDATE)
    }

    private fun setItemPendingIntent(builder: Notification.Builder, item: Item? = null) {
        val intent = Intent(mContext, TabHomeActivity::class.java)

        if( item != null ) {
            val bundle = Bundle()
            bundle.putInt("target", item.index)
            intent.putExtras(bundle)
        }

        val pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentIntent(pendingIntent)
    }


    fun postNotification(notification: Notification, id: Int) {
        val managerCompat = NotificationManagerCompat.from(mContext)
        managerCompat.notify(id, notification)
    }

    fun cancelNotification(id: Int) {
        val dispatcher = App.application.dispatcher
        dispatcher.cancel(ReminderJob.getTag(id))
    }


    fun postNotification(id: Int) {
        val item = App.application.databaseController.getScheduleItemFromId(id = id) ?: return

        val managerCompat = NotificationManagerCompat.from(mContext)
        managerCompat.notify(id, getItemNotification(item))
    }

    companion object {

        private val NOTIFICATION_SCHEDULE_UPDATE = -1
    }
}
