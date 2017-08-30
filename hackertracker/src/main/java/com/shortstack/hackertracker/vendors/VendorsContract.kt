package com.shortstack.hackertracker.vendors

import com.shortstack.hackertracker.BasePresenter
import com.shortstack.hackertracker.BaseView
import com.shortstack.hackertracker.Model.Vendor

interface VendorsContract {

    interface View : BaseView<Presenter> {

        fun setProgressIndicator( active : Boolean )

        fun showVendors( vendors : Array<Vendor> )

        fun showLoadingVendorsError()

        fun isActive(): Boolean

    }

    interface Presenter : BasePresenter

}