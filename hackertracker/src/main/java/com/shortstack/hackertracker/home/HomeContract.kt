package com.shortstack.hackertracker.home

import com.shortstack.hackertracker.BasePresenter
import com.shortstack.hackertracker.BaseView
import com.shortstack.hackertracker.Model.Item

interface HomeContract {

    interface View : BaseView<Presenter> {

        fun setProgressIndicator( active : Boolean )

        fun showRecentUpdates( items : Array<Item> )

        fun showLoadingRecentUpdatesError()

        fun showLastSyncTimestamp( timestamp : String )

        fun addAdapterItem( item : Any )

        fun isActive(): Boolean


    }

    interface Presenter: BasePresenter

}