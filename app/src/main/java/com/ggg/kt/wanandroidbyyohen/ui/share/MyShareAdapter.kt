package com.ggg.kt.wanandroidbyyohen.ui.share

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.databinding.ItemMyShareBinding

class MyShareAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onDeleteClick: (Article) -> Unit
) : RecyclerView.Adapter<MyShareAdapter.MyShareViewHolder>() {

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

    fun removeArticle(articleId: Int) {
        val index = articles.indexOfFirst { it.id == articleId }
        if (index == -1) return

        articles.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyShareViewHolder {
        val binding = ItemMyShareBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyShareViewHolder(binding, onItemClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: MyShareViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    class MyShareViewHolder(
        private val binding: ItemMyShareBinding,
        private val onItemClick: (Article) -> Unit,
        private val onDeleteClick: (Article) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.tvTitle.text = article.title
            binding.tvInfo.text = buildString {
                append(article.displayAuthor())
                if (!article.niceDate.isNullOrBlank()) {
                    append(" · ")
                    append(article.niceDate)
                }
            }

            binding.root.setOnClickListener {
                onItemClick(article)
            }

            binding.tvDelete.setOnClickListener {
                onDeleteClick(article)
            }
        }
    }
}