package com.shortstack.hackertracker.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewer.fromAsset(arguments.getString(ARG_PDF)).onLoad { progress_container.visibility = View.GONE }.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewer.recycle()
    }

    companion object {
        val ARG_PDF = "PDF"

        fun newInstance(file: String): MapFragment {
            val fragment = MapFragment()

            val bundle = Bundle()
            bundle.putString(ARG_PDF, file)
            fragment.arguments = bundle

            return fragment
        }
    }

}