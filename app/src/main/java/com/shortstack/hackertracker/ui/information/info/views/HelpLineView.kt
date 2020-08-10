package com.shortstack.hackertracker.ui.information.info.views

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.view_help_line.view.*


class HelpLineView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_help_line, this)

        call.setOnClickListener {
            showCallAlert()
        }
    }

    private fun showCallAlert() {
        AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setTitle(R.string.help_line_title)
                .setMessage(R.string.help_line_message)
                .setNegativeButton(R.string.cancel) { _, _ ->
                    // do nothing
                }
                .setPositiveButton(R.string.call) { _, _ ->
                    callHotline()
                }.show()
    }

    private fun callHotline() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:+1 (725) 222-0934")
        context.startActivity(intent)
    }

}