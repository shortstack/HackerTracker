package com.shortstack.hackertracker.ui.vendors

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Vendor
import kotlinx.android.synthetic.main.row_vendor.view.*

class VendorRenderer : Renderer<Vendor>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_vendor, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        super.hookListeners(rootView)
        rootView?.link?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(content.link))
            context.startActivity(intent)
        }
    }

    override fun render(payloads: List<Any>) {
        rootView.title.text = content.name
        rootView.description.text = content.description

        rootView.link.visibility = if (content.link.isNullOrBlank()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        val colours = context.resources.getStringArray(R.array.colors)
        val color = Color.parseColor(colours[content.id % colours.size])
        rootView.card.setCardBackgroundColor(color)
    }
}
