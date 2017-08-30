package com.shortstack.hackertracker.vendors

import com.shortstack.hackertracker.Application.App
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class VendorsPresenter(private val view : VendorsContract.View) : VendorsContract.Presenter {

    override fun start() {
        fetchVendors()
    }

    private fun fetchVendors() {
        view.setProgressIndicator(true)

        App.application.databaseController.getVendors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.setProgressIndicator(false)
                    view.showVendors(it.toTypedArray())
                }, {
                    if (view.isActive()) {
                        view.showLoadingVendorsError()
                    }

                })
    }

}