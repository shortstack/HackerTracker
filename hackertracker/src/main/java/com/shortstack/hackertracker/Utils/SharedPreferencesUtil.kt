package com.shortstack.hackertracker.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.models.Filter
import com.shortstack.hackertracker.R
import java.util.*
import javax.inject.Inject


class SharedPreferencesUtil @Inject constructor(context: Context) {

    enum class Key(private val tag: String) {

        USER_FILTER("user_filter"),
        USER_ALLOW_PUSH("user_allow_push_notifications"),
        USER_MILITARY_TIME("user_use_military_time"),
        USER_EXPIRED_EVENTS("user_show_expired_events"),
        USER_ANALYTICS("user_analytics"),

        APP_SYNC_SCHEDULED("app_sync_scheduled"),
        APP_LAST_SYNC_VERSION("app_last_sync_version"),
        APP_UPDATED_EVENTS("app_updated_events"),
        APP_LAST_REFRESH("app_last_refresh"),
        APP_LAST_UPDATED("app_last_updated"),
        APP_VIEW_PAGER_POSITION("app_view_pager_position"),

        APP_DATABASE_SELECTED("app_database_selected"),

        SCHEDULE_DAY_VIEW("app_day_view");

        override fun toString(): String {
            return tag
        }
    }

    private val mPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val editor: SharedPreferences.Editor
        get() = mPreferences.edit()

    var lastUpdated: String
        get() = mPreferences.getString(Key.APP_LAST_UPDATED.toString(), null)
        set(date) {
            val editor = editor
            editor.putString(Key.APP_LAST_UPDATED.toString(), date)
            editor.apply()
        }

    fun allowPushNotifications(show: Boolean) {
        val editor = editor
        editor.putBoolean(Key.USER_ALLOW_PUSH.toString(), show)
        editor.apply()
    }

    var lastSyncVersion: Int
        get() = mPreferences.getInt(Key.APP_LAST_SYNC_VERSION.toString(), 0)
        set(version) {
            editor.putInt(Key.APP_LAST_SYNC_VERSION.toString(), version).apply()
        }

    var scheduleDay: Int
        get() = mPreferences.getInt(Key.SCHEDULE_DAY_VIEW.toString(), 0)
        set(pos) {
            val editor = editor
            editor.putInt(Key.SCHEDULE_DAY_VIEW.toString(), pos)
            editor.apply()
        }

    fun allowPushNotifications(): Boolean {
        return mPreferences.getBoolean(Key.USER_ALLOW_PUSH.toString(), true)
    }

    fun shouldShowMilitaryTime(): Boolean {
        return mPreferences.getBoolean(Key.USER_MILITARY_TIME.toString(), false)
    }

    fun showExpiredEvents(): Boolean {
        return mPreferences.getBoolean(Key.USER_EXPIRED_EVENTS.toString(), false)
    }

    fun showActiveEventsOnly(): Boolean {
        return !showExpiredEvents()
    }

    fun saveFilter(filter: Filter) {
        val editor = editor
        editor.putStringSet(Key.USER_FILTER.toString(), filter.typesSet)
        editor.apply()
    }

    var filter: Filter
        get() = Filter(mPreferences.getStringSet(Key.USER_FILTER.toString(), HashSet<String>())!!)
        set(filter) = editor.putStringSet(Key.USER_FILTER.toString(), filter.typesSet).apply()

    var lastRefresh: Long
        get() = mPreferences.getLong(Key.APP_LAST_REFRESH.toString(), 0)
        set(time) = editor.putLong(Key.APP_LAST_REFRESH.toString(), time).apply()

    fun shouldRefresh(time: Long): Boolean {
        return time - mPreferences.getLong(Key.APP_LAST_REFRESH.toString(), 0) > Constants.TIMER_INTERVAL
    }

    var viewPagerPosition: Int
        get() = mPreferences.getInt(Key.APP_VIEW_PAGER_POSITION.toString(), 0)
        set(index) {
            editor.putInt(Key.APP_VIEW_PAGER_POSITION.toString(), index).commit()
        }

    val isTrackingAnalytics: Boolean
        get() = mPreferences.getBoolean(Key.USER_ANALYTICS.toString(), true)

    val isSyncScheduled: Boolean
        get() = mPreferences.getBoolean(Key.APP_SYNC_SCHEDULED.toString(), false)

    fun setSyncScheduled() {
        editor.putBoolean(Key.APP_SYNC_SCHEDULED.toString(), true).apply()
    }

    var databaseSelected: Int
        get() = mPreferences.getInt(Key.APP_DATABASE_SELECTED.toString(), 0)
        set(database) = editor.putInt(Key.APP_DATABASE_SELECTED.toString(), database).apply()


    val databaseTheme: Int
        get() = if (databaseSelected == 0) R.style.AppTheme
        else if (databaseSelected == 1) R.style.AppTheme_Toorcon
        else if (databaseSelected == 2) R.style.AppTheme_Shmoocon
        else if (databaseSelected == 3) R.style.AppTheme_Hackwest
        else if (databaseSelected == 4) R.style.AppTheme_LayerOne
        else R.style.AppTheme_BsidesOrl
}
