package com.shortstack.hackertracker.views


import android.content.Context
import com.google.android.material.appbar.AppBarLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import com.shortstack.hackertracker.R

class ScrollingFABBehavior(context : Context, attrs : AttributeSet) : androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<com.google.android.material.floatingactionbutton.FloatingActionButton>(context, attrs) {
    private val toolbarHeight : Int

    init {
        this.toolbarHeight = getToolbarHeight(context)
    }

    override fun layoutDependsOn(parent : androidx.coordinatorlayout.widget.CoordinatorLayout, fab : com.google.android.material.floatingactionbutton.FloatingActionButton, dependency : View) : Boolean {
        return dependency is com.google.android.material.appbar.AppBarLayout
    }

    override fun onDependentViewChanged(parent : androidx.coordinatorlayout.widget.CoordinatorLayout, fab : com.google.android.material.floatingactionbutton.FloatingActionButton, dependency : View) : Boolean {
        if (dependency is com.google.android.material.appbar.AppBarLayout) {
            val lp = fab.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
            val fabBottomMargin = lp.bottomMargin
            val distanceToScroll = fab.height + fabBottomMargin
            val ratio = dependency.getY().toFloat() / toolbarHeight.toFloat()
            fab.translationY = -distanceToScroll * ratio
        }
        return true
    }

    private fun getToolbarHeight(context : Context) : Int {
        val styledAttributes = context.theme.obtainStyledAttributes(
                intArrayOf(R.attr.actionBarSize))
        val toolbarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        return toolbarHeight
    }
}