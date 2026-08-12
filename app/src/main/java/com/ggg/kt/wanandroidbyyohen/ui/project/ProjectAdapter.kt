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
import com.ggg.kt.wanandroidbyyohen.data.collect.ArticleCollectState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.databinding.ItemProjectBinding

class ProjectAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onCollectClick: ((Article) -> Unit)? = null
) : ListAdapter<Article, ProjectAdapter.ProjectViewHolder>(ProjectDiffCallback()) {

    private var collectStates: Map<Int, ArticleCollectState> = emptyMap()

    fun addList(newList: List<Article>) {
        if (newList.isEmpty()) return
        submitList(currentList + newList)
    }

    fun updateCollectStates(
        newStates: Map<Int, ArticleCollectState>
    ) {
        val previousStates = collectStates
        collectStates = newStates

        currentList.forEachIndexed { index, article ->
            val previousState = previousStates[article.id]
                ?: ArticleCollectState(isCollected = article.collect)

            val newState = newStates[article.id]
                ?: ArticleCollectState(isCollected = article.collect)

            if (previousState != newState) {
                notifyItemChanged(index, PAYLOAD_COLLECT)
            }
        }
    }

    private fun collectStateOf(
        article: Article
    ): ArticleCollectState {
        return collectStates[article.id]
            ?: ArticleCollectState(isCollected = article.collect)
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

    override fun onBindViewHolder(
        holder: ProjectViewHolder,
        position: Int
    ) {
        val article = getItem(position)

        holder.bind(
            article = article,
            collectState = collectStateOf(article)
        )
    }

    override fun onBindViewHolder(
        holder: ProjectViewHolder,
        position: Int,
        payloads: List<Any?>
    ) {
        val article = getItem(position)
        val collectState = collectStateOf(article)

        if (payloads.contains(PAYLOAD_COLLECT)) {
            holder.bindCollect(
                article = article,
                collectState = collectState
            )
        } else {
            holder.bind(
                article = article,
                collectState = collectState
            )
        }
    }

    class ProjectViewHolder(
        private val binding: ItemProjectBinding,
        private val onItemClick: (Article) -> Unit,
        private val onCollectClick: ((Article) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            article: Article,
            collectState: ArticleCollectState
        ) {
            binding.tvAuthor.text = article.displayAuthor()
            binding.tvTitle.text = article.title
            binding.tvDesc.text = article.desc.orEmpty()
            binding.tvDate.text = article.niceDate.orEmpty()

            binding.ivCover.load(article.envelopePic) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }

            bindCollect(
                article = article,
                collectState = collectState
            )

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }

        fun bindCollect(
            article: Article,
            collectState: ArticleCollectState
        ) {
            binding.ivCollect.setImageResource(
                if (collectState.isCollected) {
                    R.drawable.ic_heart_filled
                } else {
                    R.drawable.ic_heart_line
                }
            )

            binding.ivCollect.isEnabled = !collectState.isPending

            binding.ivCollect.alpha =
                if (collectState.isPending) 0.5f else 1f

            binding.ivCollect.contentDescription =
                if (collectState.isCollected) {
                    "取消收藏"
                } else {
                    "收藏"
                }

            binding.ivCollect.setOnClickListener {
                onCollectClick?.invoke(article.copy(collect = collectState.isCollected))
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
