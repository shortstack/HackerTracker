package com.shortstack.hackertracker.ui.vendors

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Vendor
import kotlinx.android.synthetic.main.row_vendor.view.*

class VendorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_vendor, parent, false)
    }

    fun render(vendor: Vendor) {
        view.title.text = vendor.name
        view.description.text = if (vendor.description.isNullOrBlank()) {
            "Nothing to say."
        } else {
            vendor.description
        }

        view.link.visibility = if (vendor.link.isNullOrBlank()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        val colours = view.context.resources.getStringArray(R.array.colors)
        val color = Color.parseColor(colours[vendor.id % colours.size])
        view.card.setCardBackgroundColor(color)

        view.link.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(vendor.link))
            view.context.startActivity(intent)
        }
    }
}
