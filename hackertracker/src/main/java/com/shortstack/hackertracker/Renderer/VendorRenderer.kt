package com.shortstack.hackertracker.Renderer

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.BottomSheet.VendorBottomSheetDialogFragment
import com.shortstack.hackertracker.Model.Vendors
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.row_vendor.view.*

class VendorRenderer : Renderer<Vendors.Vendor>(), View.OnClickListener {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_vendor, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        super.hookListeners(rootView)
        rootView!!.setOnClickListener(this)
    }

    override fun render(payloads: List<Any>) {
        rootView.title!!.text = content.title
        rootView.description!!.text = content.description
        rootView.partner!!.visibility = if (content.isPartner) View.VISIBLE else View.GONE
    }


    override fun onClick(view: View) {
        val bottomSheetDialogFragment = VendorBottomSheetDialogFragment.newInstance(content)
        bottomSheetDialogFragment.show((context as AppCompatActivity).supportFragmentManager, bottomSheetDialogFragment.tag)
    }
}
