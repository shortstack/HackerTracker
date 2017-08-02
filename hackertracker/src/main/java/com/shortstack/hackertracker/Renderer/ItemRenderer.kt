package com.shortstack.hackertracker.Renderer

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.BottomSheet.ScheduleItemBottomSheetDialogFragment
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.row.view.*

class ItemRenderer : Renderer<Item>(), View.OnClickListener {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView!!.setOnClickListener(this)
    }

    override fun render(payloads: List<Any>) {
        rootView.item.setItem(content)
    }


    override fun onClick(view: View) {
        val bottomSheetDialogFragment = ScheduleItemBottomSheetDialogFragment.newInstance(content)
        bottomSheetDialogFragment.show((context as AppCompatActivity).supportFragmentManager, bottomSheetDialogFragment.tag)
    }
}
