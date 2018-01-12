package com.shortstack.hackertracker.home

import android.content.Context
import com.pedrogomez.renderers.RendererContent
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Fragment.InformationFragment
import com.shortstack.hackertracker.Model.Navigation
import com.shortstack.hackertracker.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


// TODO: Should not pass Context, use DI.
class HomePresenter(private val context : Context, private val view : HomeContract.View) : HomeContract.Presenter {
    override fun start() {
        fetchRecentUpdates()
    }

    fun fetchRecentUpdates() {
        view.setProgressIndicator(true)


        // TODO: This should use DI.
        App.application.databaseController.getRecent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.setProgressIndicator(false)

                    // TODO: This could be a lot clearer.
                    view.addAdapterItem(getHeader())
                    view.addAdapterItem(getInformationNav())
                    view.addAdapterItem(getChangeConCard())

                    view.showLastSyncTimestamp(getLastSyncTimestamp())

                    view.showRecentUpdates(it.toTypedArray())
                }, {
                    if (view.isActive()) {
                        view.setProgressIndicator(false)
                        view.showLoadingRecentUpdatesError()
                    }
                })

    }

    private fun getHeader() : Any {
        return RendererContent<Void>(null, HomeFragment.TYPE_HEADER)
    }

    private fun getChangeConCard() : Any {
        return RendererContent<Void>(null, HomeFragment.TYPE_CHANGE_CON)
    }

    private fun getInformationNav() = Navigation(context.getString(R.string.nav_help_title), context.getString(R.string.nav_help_body), InformationFragment::class.java)

    private fun getLastSyncTimestamp() : String {
        val storage = App.application.storage
        val lastDate : String

        val cal = Calendar.getInstance()
        cal.time = Date(storage.lastRefresh)

        val refresh = storage.lastRefresh
        if (refresh == 0L) {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(storage.lastUpdated)
            lastDate = "Last synced " + App.getRelativeDateStamp(date)
        } else {
            lastDate = "Last synced " + App.getRelativeDateStamp(Date(refresh))
        }

        return "Last updated\n" + lastDate.toLowerCase()
    }
}