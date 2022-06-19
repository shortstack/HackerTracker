package com.shortstack.hackertracker.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.databinding.ItemArticleBinding
import com.shortstack.hackertracker.models.local.Article

class ArticleViewHolder(private val binding: ItemArticleBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(article: Article) {
        binding.title.text = article.name
        binding.content.text = article.text
    }

    companion object {
        fun inflate(parent: ViewGroup): ArticleViewHolder {
            val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ArticleViewHolder(binding)
        }
    }
}