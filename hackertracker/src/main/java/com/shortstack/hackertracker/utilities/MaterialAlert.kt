package com.shortstack.hackertracker.utilities

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import androidx.appcompat.app.AlertDialog
import com.shortstack.hackertracker.R

class MaterialAlert(private val context: Context) {

    companion object {

        fun create(context: Context): MaterialAlert {
            return MaterialAlert(context)
        }
    }

    private val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.MyAlertDialogStyle)

    fun setTitle(title: Int): MaterialAlert {
        builder.setTitle(getString(title))
        return this
    }

    fun setTitle(title: String): MaterialAlert {
        builder.setTitle(title)
        return this
    }

    fun setMessage(message: Int): MaterialAlert {
        setMessage(getString(message))
        return this
    }

    fun setMessage(message: String): MaterialAlert {
        builder.setMessage(message)
        return this
    }

    fun setItems(items: List<Item>, listener: DialogInterface.OnClickListener): MaterialAlert {
        val adapter = object : ArrayAdapter<Item>(
                context,
                android.R.layout.select_dialog_singlechoice,
                android.R.id.text1,
                items) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                val textView = v.findViewById<CheckedTextView>(android.R.id.text1)
                textView.text = items[position].text
                textView.isChecked = items[position].isChecked

                return v
            }

        }

        builder.setAdapter(adapter, listener)
        return this
    }

    fun setPositiveButton(text: Int, listener: DialogInterface.OnClickListener): MaterialAlert {
        builder.setPositiveButton(getString(text), listener)
        return this
    }

    fun setNegativeButton(text: Int, listener: DialogInterface.OnClickListener): MaterialAlert {
        builder.setNegativeButton(getString(text), listener)
        return this
    }

    private fun getString(text: Int): String {
        return context.getString(text)
    }

    fun build(): AlertDialog {
        return builder.create()
    }

    fun show() {
        build().show()
    }

    data class Item(val text: String, val isChecked: Boolean) {
        override fun toString() = text
    }


}