package com.shortstack.hackertracker.ui.information

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Information
import kotlinx.android.synthetic.main.row_info.view.*

class InformationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): InformationViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_info, parent, false)
            return InformationViewHolder(view)
        }
    }

    fun render(information: Information) {
        view.header.text = information.title

        view.setOnClickListener {
            showInformationBottomSheet(information)
        }
    }

    private fun showInformationBottomSheet(information: Information) {
        val badges = InformationBottomSheet.newInstance(information)
        badges.show((view.context as AppCompatActivity).supportFragmentManager, badges.tag)
    }
}
