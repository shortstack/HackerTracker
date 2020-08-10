package com.shortstack.hackertracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Article
import kotlinx.android.synthetic.main.item_article.view.*

class ArticleViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): ArticleViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
            return ArticleViewHolder(view)
        }
    }

    fun render(article: Article) {
        view.title.text = article.name
        view.content.text = article.text
    }
}