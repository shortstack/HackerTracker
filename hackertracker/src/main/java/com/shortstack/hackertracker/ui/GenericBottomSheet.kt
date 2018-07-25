package com.shortstack.hackertracker.ui

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.text.TextUtils
import android.view.View
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utils.MaterialAlert
import kotlinx.android.synthetic.main.bottom_sheet_generic.view.*
import kotlinx.android.synthetic.main.empty_text.view.*


abstract class GenericBottomSheet : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    override fun setupDialog(dialog : Dialog, style : Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.bottom_sheet_generic, null)
        dialog.setContentView(view)

        view.title!!.text = getTitle()

        val isDescriptionEmpty = TextUtils.isEmpty(getDescription())
        view.empty!!.visibility = if (isDescriptionEmpty) View.VISIBLE else View.GONE
        view.description!!.text = getDescription()

        view.link!!.visibility = if (hasLink()) View.VISIBLE else View.GONE
        view.link.setOnClickListener { onLinkClick() }
    }

    protected abstract fun getLink() : String?

    protected abstract fun getTitle() : String

    protected abstract fun getDescription() : String

    protected abstract fun hasLink() : Boolean

    fun onLinkClick() {
        val context = context ?: return

        MaterialAlert.create(context)
                .setTitle(R.string.link_warning)
                .setMessage(String.format(context.getString(R.string.link_message), getLink()?.toLowerCase()))
                .setPositiveButton(R.string.open_link, DialogInterface.OnClickListener {
                    _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(getLink()))
                    context.startActivity(intent)
                }).setBasicNegativeButton()
                .show()
    }
}
