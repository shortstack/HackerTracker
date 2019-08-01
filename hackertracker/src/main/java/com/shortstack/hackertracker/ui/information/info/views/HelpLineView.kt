package com.shortstack.hackertracker.ui.information.info.views

import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utilities.MaterialAlert
import kotlinx.android.synthetic.main.view_help_line.view.*
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


class HelpLineView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_help_line, this)

        call.setOnClickListener {
            showCallAlert()
        }
    }

    private fun showCallAlert() {
        MaterialAlert.create(context)
                .setTitle(R.string.help_line_title)
                .setMessage(R.string.help_line_message)
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ ->
                    // do nothing
                })
                .setPositiveButton(R.string.call, DialogInterface.OnClickListener { _, _ ->
                    callHotline()
                }).show()
    }

    private fun callHotline() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:+1 (725) 222-0934")
        context.startActivity(intent)
    }

}