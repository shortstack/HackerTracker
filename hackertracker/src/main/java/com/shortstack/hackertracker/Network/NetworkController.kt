package com.shortstack.hackertracker.Network

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AlertDialog

import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Alert.MaterialAlert
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.Event.UpdateListContentsEvent
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.R

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class NetworkController(private val context: Context) : Callback<SyncResponse> {

    private var dialog: AlertDialog? = null

    fun syncInForeground(context: Context) {
        showSyncingDialog(context)
        sync()
    }

    fun syncInBackground() {
        sync()
    }

    private fun sync() {
        Logger.d("Syncing to server.")
        val retrofit = Retrofit.Builder().baseUrl(Constants.API_URL_BASE).addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(HTService::class.java)
        service.sync.enqueue(this)
    }


    private fun showSyncingDialog(context: Context) {
        dialog = MaterialAlert.create(context).setTitle(this.context.getString(R.string.sync_title)).setMessage(this.context.getString(R.string.sync_init)).build()
        dialog!!.show()
    }

    override fun onResponse(call: Call<SyncResponse>, response: Response<SyncResponse>) {
        if (response.isSuccessful) {
            updateDatabase(response.body().schedule)
        } else {
            setDialogMessage(context.getString(R.string.sync_error))
        }
    }

    private fun updateDatabase(schedule: Array<Item>) {
        object : AsyncTask<Void, Int, Void?>() {

            override fun doInBackground(vararg params: Void): Void? {
                val time = System.currentTimeMillis()

                val database = App.application.databaseController

                // Remove, only for debugging.
                val stringArray = context.resources.getStringArray(R.array.filter_types)
                val locationArray = arrayOf("Track 1", "Track 2", "Track 3", "DEFCON 101")
                var index = 0


                for (i in schedule.indices) {
                    val scheduleObject = schedule[i]

                    // Remove, only for debugging.
                    scheduleObject.type = stringArray[index]
                    index = ++index % stringArray.size
                    scheduleObject.location = locationArray[index % locationArray.size]


                    database.updateScheduleItem(scheduleObject)

                    publishProgress(i)
                }

                Logger.d("Total update time: " + (System.currentTimeMillis() - time))
                return null
            }



            override fun onProgressUpdate(vararg values: Int?) {
                super.onProgressUpdate(*values)
                setDialogMessage(getProgress(*values))
            }

            private fun getProgress(vararg values: Int?): String {
                return String.format(context.getString(R.string.update_progress), values[0], schedule.size)
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                setDialogMessage(context.getString(R.string.sync_done))

                App.application.postBusEvent(UpdateListContentsEvent())
            }
        }.execute()
    }


    override fun onFailure(call: Call<SyncResponse>, t: Throwable) {
        Logger.d("Network Sync onFailure: " + t.message)
        setDialogMessage(context.getString(R.string.sync_error) + if (BuildConfig.DEBUG) "\n\nonFailure:\n" + t.message else "")
    }

    private fun setDialogMessage(message: String) {
        dialog?.setMessage(message)
    }
}
