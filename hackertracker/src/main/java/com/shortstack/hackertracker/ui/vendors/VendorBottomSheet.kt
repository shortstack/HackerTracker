package com.shortstack.hackertracker.ui.vendors

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.text.TextUtils
import android.view.View
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.models.Vendor
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utils.MaterialAlert
import kotlinx.android.synthetic.main.bottom_sheet_generic.view.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*

class VendorBottomSheet : BottomSheetDialogFragment() {

    override fun setupDialog(dialog : Dialog, style : Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.bottom_sheet_generic, null)
        dialog.setContentView(view)

        val vendor = content

        if (vendor == null) {
            Logger.e("Vendor is null. Can not render bottom sheet.")
            return
        }

        view.title!!.text = vendor.title

        val isDescriptionEmpty = TextUtils.isEmpty(vendor.description)
        view.empty!!.visibility = if (isDescriptionEmpty) View.VISIBLE else View.GONE
        view.description!!.text = vendor.description

        view.link.setOnClickListener { onLinkClick() }
    }

    private val content : Vendor?
        get() = arguments.getSerializable(ARG_VENDOR) as Vendor

    fun onLinkClick() {
        MaterialAlert.create(context)
                .setTitle(R.string.link_warning)
                .setMessage(String.format(context.getString(R.string.link_message), content!!.link!!.toLowerCase()))
                .setPositiveButton(R.string.open_link, DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(content!!.link))
                    context.startActivity(intent)
                }).setBasicNegativeButton()
                .show()
    }

    companion object {

        val ARG_VENDOR = "VENDOR"


        fun newInstance(vendor : Vendor) : VendorBottomSheet {
            val fragment = VendorBottomSheet()

            val bundle = Bundle()
            bundle.putSerializable(ARG_VENDOR, vendor)
            fragment.arguments = bundle

            return fragment
        }
    }
}
