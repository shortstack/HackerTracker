package com.shortstack.hackertracker.view


import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import com.shortstack.hackertracker.R

class ScrollingFABBehavior(context : Context, attrs : AttributeSet) : CoordinatorLayout.Behavior<FloatingActionButton>(context, attrs) {
    private val toolbarHeight : Int

    init {
        this.toolbarHeight = getToolbarHeight(context)
    }

    override fun layoutDependsOn(parent : CoordinatorLayout, fab : FloatingActionButton, dependency : View) : Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent : CoordinatorLayout, fab : FloatingActionButton, dependency : View) : Boolean {
        if (dependency is AppBarLayout) {
            val lp = fab.layoutParams as CoordinatorLayout.LayoutParams
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