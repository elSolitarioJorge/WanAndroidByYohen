package com.ggg.kt.wanandroidbyyohen.ui.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.databinding.ItemProjectBinding

class ProjectAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onCollectClick: ((Article) -> Unit)? = null
) : ListAdapter<Article, ProjectAdapter.ProjectViewHolder>(ProjectDiffCallback()) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemProjectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ProjectViewHolder(
            binding = binding,
            onItemClick = onItemClick,
            onCollectClick = onCollectClick
        )
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int, payloads: List<Any?>) {
        if (payloads.contains(PAYLOAD_COLLECT)) {
            holder.bindCollect(getItem(position))
        } else {
            holder.bind(getItem(position))
        }
    }

    class ProjectViewHolder(
        private val binding: ItemProjectBinding,
        private val onItemClick: (Article) -> Unit,
        private val onCollectClick: ((Article) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.tvAuthor.text = article.displayAuthor()
            binding.tvTitle.text = article.title
            binding.tvDesc.text = article.desc.orEmpty()
            binding.tvDate.text = article.niceDate.orEmpty()

            binding.ivCover.load(article.envelopePic) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }

            bindCollect(article)

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }

        fun bindCollect(article: Article) {
            binding.ivCollect.setImageResource(
                if (article.collect) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_line
            )
            binding.ivCollect.setOnClickListener {
                onCollectClick?.invoke(article)
            }
        }
    }

    companion object {
        private const val PAYLOAD_COLLECT = "payload_collect"
    }

    private class ProjectDiffCallback : DiffUtil.ItemCallback<Article>() {
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
