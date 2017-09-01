package com.shortstack.hackertracker.vendors

import com.shortstack.hackertracker.Database.DEFCONDatabaseController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class VendorsPresenter(@Inject private val database : DEFCONDatabaseController) : VendorsContract.Presenter {

    private var view : VendorsContract.View? = null

    override fun <T> takeView(view : T) {
        this.view = view as VendorsContract.View
        loadVendors()
    }

    override fun dropView() {
        view = null
    }

    private fun loadVendors() {
        view?.setProgressIndicator(true)

        database.getVendors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.setProgressIndicator(false)
                    view?.showVendors(it.toTypedArray())
                }, {
                    if (view?.isActive() ?: false) {
                        view?.setProgressIndicator(false)
                        view?.showLoadingVendorsError()
                    }

                })
    }

}