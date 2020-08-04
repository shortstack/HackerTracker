package com.shortstack.hackertracker.ui.information.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.events.EventDetailsAdapter
import kotlinx.android.synthetic.main.empty_text.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_category.collapsing_toolbar
import kotlinx.android.synthetic.main.fragment_category.description
import kotlinx.android.synthetic.main.fragment_category.toolbar
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.row_vendor.*

class CategoryFragment : Fragment() {

    companion object {
        private const val EXTRA_TYPE = "EXTRA_CATEGORY"

        fun newInstance(type: Type): CategoryFragment {
            val fragment = CategoryFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_TYPE, type)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = context ?: return

        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_white_24dp)
        toolbar.navigationIcon = drawable

        toolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.popBackStack()
        }

        val type = arguments?.getParcelable(EXTRA_TYPE) as? Type
        if (type != null) {
            showType(type)
        }
    }

    private fun showType(type: Type) {
        collapsing_toolbar.title = type.fullName

        val body = type.description

        if (body.isNotBlank()) {
            empty.visibility = View.GONE
            description.text = body
        } else {
            empty.visibility = View.VISIBLE
        }

        val adapter = EventDetailsAdapter()
        adapter.setElements(type.actions, emptyList())
        links.adapter = adapter
        val gridLayoutManager = links.layoutManager as GridLayoutManager

        gridLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return adapter.getSpanSize(position, gridLayoutManager.spanCount)
                }
            }
    }
}