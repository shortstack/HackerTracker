package com.shortstack.hackertracker.Activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import com.shortstack.hackertracker.Analytics.AnalyticsController
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Fragment.AuthorFragment
import com.shortstack.hackertracker.Fragment.DescriptionFragment
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Model.ItemViewModel
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(toolbar)

        val obj = ItemViewModel(content)

        App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_VIEW, content)

        item!!.setItem(obj.item)


        val color = resources.getIntArray(R.array.colors) [obj.categoryColorPosition]
        tab_layout.setBackgroundColor(color)
        toolbar.setBackgroundColor(color)



        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = content.type

        val adapter = PagerAdapter(supportFragmentManager, content)
        pager.adapter = adapter


        tab_layout.addTab(tab_layout.newTab().setText("Description"))
        tab_layout.addTab(tab_layout.newTab().setText("Author"))
        tab_layout.tabGravity = TabLayout.GRAVITY_FILL
    }


    private val content : Item
        get() = intent.extras.getSerializable("ARG_ITEM") as Item


    class PagerAdapter(fm : FragmentManager, val item : Item) : FragmentStatePagerAdapter(fm) {

        val NUM_OF_PAGES = 2
        val DESCRIPTION = 0
        val AUTHOR = 1

        override fun getItem(position : Int) : Fragment {
            return when (position) {
                DESCRIPTION -> DescriptionFragment.newInstance(item)
                AUTHOR -> AuthorFragment.newInstance(item)
                else -> {
                    throw IllegalArgumentException("Could not find fragment for position $position.")
                }
            }

        }

        override fun getCount() : Int {
            return NUM_OF_PAGES
        }

    }
}
