package com.ggg.kt.wanandroidbyyohen.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.databinding.ItemArticleBinding

class HomeArticleAdapter(
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<HomeArticleAdapter.ArticleViewHolder>() {
    private val articles = mutableListOf<Article>()

    fun submitList(newList: List<Article>) {
        articles.clear()
        articles.addAll(newList)
        notifyDataSetChanged()
    }

    fun addList(newList: List<Article>) {
        if (newList.isEmpty()) return
        val startPosition = articles.size
        articles.addAll(newList)
        notifyItemRangeChanged(startPosition, newList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount() : Int = articles.size

    class ArticleViewHolder(
        private val binding: ItemArticleBinding,
        private val onItemClick: (Article) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            binding.tvTitle.text = if (article.isTop) {
                "【置顶】${article.title}"
            } else {
                article.title
            }
            binding.tvInfo.text = "${article.displayAuthor()} · ${article.niceDate.orEmpty()}"

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }
}