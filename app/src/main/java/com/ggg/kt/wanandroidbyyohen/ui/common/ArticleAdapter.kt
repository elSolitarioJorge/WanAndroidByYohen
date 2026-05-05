package com.ggg.kt.wanandroidbyyohen.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.extension.toHighlightText
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.databinding.ItemArticleBinding

class ArticleAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onCollectClick: ((Article) -> Unit)
) : ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    fun addList(newList: List<Article>) {
        if (newList.isEmpty()) return
        submitList(currentList + newList)
    }

    fun updateCollectState(articleId: Int, collect: Boolean) {
        val newList = currentList.map { article ->
            if (article.id == articleId) {
                article.copy(collect = collect)
            } else {
                article
            }
        }
        submitList(newList)
    }

    fun removeArticle(articleId: Int) {
        val newList = currentList.filter { it.id != articleId }
        submitList(newList)
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
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int, payloads: List<Any?>) {
        if (payloads.contains(PAYLOAD_COLLECT)) {
            holder.bindCollect(getItem(position))
        } else {
            holder.bind(getItem(position))
        }
    }

    class ArticleViewHolder(
        private val binding: ItemArticleBinding,
        private val onItemClick: (Article) -> Unit,
        private val onCollectClick: ((Article) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.tvAuthor.text = article.displayAuthor()
            binding.tvTagTop.visibility = if (article.isTop) View.VISIBLE else View.GONE
            binding.tvTagCategory.text = if (!article.chapterName.isNullOrBlank()) article.chapterName else "未知"
            binding.tvCategory.text = if (!article.superChapterName.isNullOrBlank()) article.superChapterName else "未知"
            binding.tvTitle.text = article.title.toHighlightText()
            binding.tvTime.text = if (!article.niceDate.isNullOrBlank()) article.niceDate else "未知"

            bindCollect(article)
            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }

        fun bindCollect(article: Article) {
            binding.btnCollect.setImageResource(
                if (article.collect) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_line
            )
            binding.btnCollect.setOnClickListener {
                onCollectClick.invoke(article)
            }
        }
    }

    companion object {
        private const val PAYLOAD_COLLECT = "payload_collect"
    }

    private class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Article, newItem: Article): Any? {
            return if (oldItem.collect != newItem.collect) {
                PAYLOAD_COLLECT
            } else {
                null
            }
        }
    }
}
