package com.shortstack.hackertracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.item_article.view.*

class ArticleViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): ArticleViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
            return ArticleViewHolder(view)
        }
    }

    fun render(title: String, content: String) {
        view.title.text = title
        view.content.text = content
    }
}