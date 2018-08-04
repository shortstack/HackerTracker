package com.shortstack.hackertracker.ui.vendors

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.text.TextUtils
import android.view.View
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.models.Vendor
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utils.MaterialAlert
import kotlinx.android.synthetic.main.bottom_sheet_generic.view.*
import kotlinx.android.synthetic.main.empty_text.view.*

class VendorBottomSheet : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.bottom_sheet_generic, null)
        dialog.setContentView(view)

        val vendor = content

        if (vendor == null) {
            Logger.e("Vendor is null. Can not render bottom sheet.")
            return
        }

        view.title.text = vendor.name

        val isDescriptionEmpty = TextUtils.isEmpty(vendor.description)
        view.empty.visibility = if (isDescriptionEmpty) View.VISIBLE else View.GONE
        view.description.text = vendor.description

        if(vendor.link.isNullOrBlank()) {
            view.link.visibility = View.GONE
        } else {
            view.link.visibility = View.VISIBLE
            view.link.setOnClickListener { onLinkClick() }
        }


    }

    private val content: Vendor?
        get() = arguments?.getParcelable(ARG_VENDOR)

    private fun onLinkClick() {
        val context = context ?: return
        val link = content?.link ?: return
        
        MaterialAlert.create(context)
                .setTitle(R.string.link_warning)
                .setMessage(String.format(context.getString(R.string.link_message), link.toLowerCase()))
                .setPositiveButton(R.string.open_link, DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(link))
                    context.startActivity(intent)
                }).setBasicNegativeButton()
                .show()
    }

    companion object {

        private const val ARG_VENDOR = "VENDOR"

        fun newInstance(vendor: Vendor): VendorBottomSheet {
            val fragment = VendorBottomSheet()

            val bundle = Bundle()
            bundle.putParcelable(ARG_VENDOR, vendor)
            fragment.arguments = bundle

            return fragment
        }
    }
}
