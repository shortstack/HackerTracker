package com.shortstack.hackertracker.ui.schedule.renderers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Event
import kotlinx.android.synthetic.main.row.view.*

class ItemRenderer : Renderer<Event>(), View.OnClickListener {

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
//        val bottomSheetDialogFragment = ScheduleItemBottomSheet.newInstance(content)
//        bottomSheetDialogFragment.show((context as AppCompatActivity).supportFragmentManager, bottomSheetDialogFragment.tag)
    }
}
