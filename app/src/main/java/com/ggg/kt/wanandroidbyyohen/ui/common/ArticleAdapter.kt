package com.ggg.kt.wanandroidbyyohen.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.databinding.ItemArticleBinding

class ArticleAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onCollectClick: ((Article) -> Unit)? = null
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
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
        notifyItemRangeInserted(startPosition, newList.size)
    }

    fun updateCollectState(articleId: Int, collect: Boolean) {
        val index = articles.indexOfFirst { it.id == articleId }
        if (index == -1) return

        articles[index] = articles[index].copy(collect = collect)
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(
            binding = binding,
            onItemClick = onItemClick,
            onCollectClick = onCollectClick
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    class ArticleViewHolder(
        private val binding: ItemArticleBinding,
        private val onItemClick: (Article) -> Unit,
        private val onCollectClick: ((Article) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.tvTitle.text = if (article.isTop) {
                "【置顶】${article.title}"
            } else {
                article.title
            }

            binding.tvInfo.text = buildString {
                append(article.displayAuthor())
                if (!article.niceDate.isNullOrBlank()) {
                    append(" · ")
                    append(article.niceDate)
                }
                if (!article.chapterName.isNullOrBlank()) {
                    append(" · ")
                    append(article.chapterName)
                }
            }

            binding.btnCollect.text = if (article.collect) "♥" else "♡"
            binding.btnCollect.setTextColor(
                if (article.collect) {
                    0xFFE91E63.toInt()
                } else {
                    0xFF999999.toInt()
                }
            )

            binding.root.setOnClickListener {
                onItemClick(article)
            }

            binding.btnCollect.visibility = if (onCollectClick == null) {
                View.GONE
            } else {
                View.VISIBLE
            }

            binding.btnCollect.setOnClickListener {
                onCollectClick?.invoke(article)
            }
        }
    }

}