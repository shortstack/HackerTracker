package com.shortstack.hackertracker.Alert

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.shortstack.hackertracker.R

class MaterialAlert(private val mContext : Context) {

    private var mBuilder : AlertDialog.Builder = AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle)

    fun setTitle(title : Int) : MaterialAlert {
        mBuilder.setTitle(getString(title))
        return this
    }

    fun setTitle(title : String) : MaterialAlert {
        mBuilder.setTitle(title)
        return this
    }

    fun setMessage(message : Int) : MaterialAlert {
        setMessage(getString(message))
        return this
    }

    fun setMessage(message : String) : MaterialAlert {
        mBuilder.setMessage(message)
        return this
    }

    fun setItems(items : Int, listener : DialogInterface.OnClickListener) : MaterialAlert {
        mBuilder.setItems(items, listener)
        return this
    }

    fun setItems(items : Array<Item>, listener : DialogInterface.OnClickListener) : MaterialAlert {
        val adapter = object : ArrayAdapter<Item>(
                mContext,
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items) {
            override fun getView(position : Int, convertView : View?, parent : ViewGroup) : View {
                //Use super class to create the View
                val v = super.getView(position, convertView, parent)
                val tv = v.findViewById<View>(android.R.id.text1) as TextView

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0)

                //Add margin between image and text (support various screen densities)
                val dp5 = (5 * mContext.resources.displayMetrics.density + 0.5f).toInt()
                tv.compoundDrawablePadding = dp5

                return v
            }
        }

        mBuilder.setAdapter(adapter, listener)
        return this
    }

    fun setPositiveButton(text : Int, listener : DialogInterface.OnClickListener) : MaterialAlert {
        mBuilder.setPositiveButton(getString(text), listener)
        return this
    }

    fun setNegativeButton(text : Int, listener : DialogInterface.OnClickListener) : MaterialAlert {
        mBuilder.setNegativeButton(getString(text), listener)
        return this
    }

    fun setBasicNegativeButton(text : Int) : MaterialAlert {
        mBuilder.setNegativeButton(getString(text)) { dialog, which -> dialog.dismiss() }
        return this
    }

    fun setBasicNegativeButton() : MaterialAlert {
        setBasicNegativeButton(R.string.cancel)
        return this
    }

    fun setView(view : View) : MaterialAlert {
        mBuilder.setView(view)
        return this
    }

    private fun getString(text : Int) : String {
        return mContext.getString(text)
    }

    fun build() : AlertDialog {
        //if( !mHasPositiveButton ) setBasicPositiveButton();
        return mBuilder.create()
    }

    fun setBasicPositiveButton() : MaterialAlert {
        mBuilder.setPositiveButton(getString(R.string.okay)) { dialog, which -> dialog.dismiss() }
        return this
    }

    fun setDismissCallback(listener : DialogInterface.OnDismissListener) : MaterialAlert {
        mBuilder.setOnDismissListener(listener)
        return this
    }

    fun show() {
        build().show()
    }

    class Item {
        val text : String
        val icon : Int

        constructor(text : String, icon : Int?) {
            this.text = text
            this.icon = icon!!
        }

        constructor(text : String) {
            this.text = text
            this.icon = 0
        }

        override fun toString() : String {
            return text
        }
    }

    companion object {

        fun create(context : Context) : MaterialAlert {
            return MaterialAlert(context)
        }
    }
}