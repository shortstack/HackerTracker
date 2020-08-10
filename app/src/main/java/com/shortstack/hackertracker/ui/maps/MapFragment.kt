package com.shortstack.hackertracker.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.fragment_map.*
import java.io.File

class MapFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val file = arguments?.getSerializable(ARG_PDF) as? File

        if (file == null) {
            progress.visibility = View.VISIBLE
        } else {
            progress.visibility = View.VISIBLE

            viewer.fromFile(file).onLoad {
                progress.visibility = View.GONE
            }.load()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (viewer != null)
            viewer.recycle()
    }

    companion object {
        private const val ARG_PDF = "PDF"

        fun newInstance(file: File?): MapFragment {
            val fragment = MapFragment()

            val bundle = Bundle()
            bundle.putSerializable(ARG_PDF, file)
            fragment.arguments = bundle

            return fragment
        }
    }

}