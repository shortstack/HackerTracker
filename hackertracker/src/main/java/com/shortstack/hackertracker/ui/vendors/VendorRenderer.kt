package com.shortstack.hackertracker.ui.vendors

import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Vendor
import kotlinx.android.synthetic.main.row_vendor.view.*

class VendorRenderer : Renderer<Vendor>(), View.OnClickListener {

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
    }


    override fun onClick(view: View) {
        val bottomSheetDialogFragment = VendorBottomSheet.newInstance(content)
        bottomSheetDialogFragment.show((context as AppCompatActivity).supportFragmentManager, bottomSheetDialogFragment.tag)
    }
}
