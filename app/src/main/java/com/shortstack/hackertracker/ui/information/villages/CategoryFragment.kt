package com.shortstack.hackertracker.ui.information.villages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utilities.Analytics
import kotlinx.android.synthetic.main.empty_text.*
import kotlinx.android.synthetic.main.fragment_speakers.collapsing_toolbar
import kotlinx.android.synthetic.main.fragment_speakers.description
import kotlinx.android.synthetic.main.fragment_speakers.toolbar

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
        return inflater.inflate(R.layout.fragment_speakers, container, false)
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

    }

    private fun showSpeaker(type: Type) {
        analytics.log("Viewing speaker ${type.name}")

        collapsing_toolbar.title = type.name
        collapsing_toolbar.subtitle = if (type.title.isEmpty()) {
            context?.getString(R.string.speaker_default_title)
        } else {
            type.title
        }

        toolbar.menu.clear()

        val url = type.twitter
        if (url.isNotEmpty()) {
            toolbar.inflateMenu(R.menu.speaker_twitter)

            toolbar.setOnMenuItemClickListener {
                val url = "https://twitter.com/" + url.replace("@", "")

                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
                context?.startActivity(intent)

                analytics.onSpeakerEvent(Analytics.SPEAKER_TWITTER, type)
                true
            }
        }


        val body = type.description

        if (body.isNotBlank()) {
            empty.visibility = View.GONE
            description.text = body
        } else {
            empty.visibility = View.VISIBLE
        }
    }
}