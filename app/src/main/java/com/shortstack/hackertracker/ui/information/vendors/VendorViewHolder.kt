package com.shortstack.hackertracker.ui.information.vendors

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Vendor
import kotlinx.android.synthetic.main.row_vendor.view.*

class VendorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): VendorViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_vendor, parent, false)
            return VendorViewHolder(view)
        }
    }

    fun render(vendor: Vendor) {
        view.title.text = vendor.name
        view.description.text = vendor.summary

        view.link.visibility = if (vendor.link.isNullOrBlank()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        view.link.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(vendor.link))
            view.context.startActivity(intent)
        }
    }
}
