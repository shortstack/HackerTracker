package com.shortstack.hackertracker.vendors

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.Model.Vendor
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.di.ActivityScoped
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import javax.inject.Inject

@ActivityScoped
class VendorsFragment @Inject constructor() : DaggerFragment(), VendorsContract.View {

    @Inject
    private lateinit var presenter : VendorsContract.Presenter


    override fun setPresenter(presenter : VendorsContract.Presenter) {
        this.presenter = presenter
    }

    override fun onCreateView(inflater : LayoutInflater?, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        return inflater!!.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onDestroy() {
        presenter.dropView()
        super.onDestroy()
    }

    override fun setProgressIndicator(active : Boolean) {
        loading_progress.visibility = if (active) View.VISIBLE else View.GONE
    }

    override fun showVendors(vendors : Array<Vendor>) {
        (list.adapter as RendererAdapter<*>).addAllAndNotify(vendors.toMutableList())
    }

    override fun showLoadingVendorsError() {
        Toast.makeText(context, "Could not load vendors.", Toast.LENGTH_SHORT).show()
    }

    override fun isActive() : Boolean {
        return isAdded
    }


    override fun onViewCreated(view : View?, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layout = LinearLayoutManager(context)
        list.layoutManager = layout

        val rendererBuilder = RendererBuilder<Any>()
                .bind(Vendor::class.java, VendorRenderer())

        val adapter = RendererAdapter<Any>(rendererBuilder)
        list.adapter = adapter
    }

    companion object {
        fun newInstance() : VendorsFragment {
            return VendorsFragment()
        }
    }
}



