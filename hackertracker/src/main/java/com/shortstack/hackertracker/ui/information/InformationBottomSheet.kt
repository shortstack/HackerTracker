package com.shortstack.hackertracker.ui.information

import android.os.Bundle
import com.shortstack.hackertracker.ui.GenericBottomSheet

import com.shortstack.hackertracker.models.Information

class InformationBottomSheet : GenericBottomSheet() {


    override fun getLink(): String? {
        return null
    }

    override fun getTitle(): String {
        return arguments?.getString(ARG_TITLE) ?: ""
    }

    override fun getDescription(): String {
        return arguments?.getString(ARG_DESC) ?: ""
    }

    override fun hasLink(): Boolean {
        return false
    }

    companion object {

        private val ARG_TITLE = "TITLE"
        private val ARG_DESC = "DESCRIPTION"

        fun newInstance(title: String, description: String): InformationBottomSheet {
            val fragment = InformationBottomSheet()

            val bundle = Bundle()
            bundle.putString(ARG_TITLE, title)
            bundle.putString(ARG_DESC, description)

            fragment.arguments = bundle

            return fragment
        }

        fun newInstance(content: Information): InformationBottomSheet {
            return newInstance(content.title, content.description)
        }
    }


}
