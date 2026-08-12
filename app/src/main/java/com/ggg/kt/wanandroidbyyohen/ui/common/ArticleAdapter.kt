package com.ggg.kt.wanandroidbyyohen.ui.common

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.extension.toHighlightText
import com.ggg.kt.wanandroidbyyohen.data.collect.ArticleCollectState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.databinding.ItemArticleBinding

class ArticleAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onActionClick: (Article) -> Unit,
    private val actionMode: ArticleActionMode = ArticleActionMode.COLLECT
) : ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    private var collectStates: Map<Int, ArticleCollectState> = emptyMap()
    enum class ArticleActionMode {
        COLLECT,
        COLLECTED,
        SHARED
    }

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
            onActionClick = onActionClick,
            actionMode = actionMode
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(
            article = article,
            collectState = collectStateOf(article)
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int, payloads: List<Any?>) {
        val article = getItem(position)
        val collectState = collectStateOf(article)
        if (payloads.contains(PAYLOAD_COLLECT)) {
            holder.bindAction(
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

    class ArticleViewHolder(
        private val binding: ItemArticleBinding,
        private val onItemClick: (Article) -> Unit,
        private val onActionClick: (Article) -> Unit,
        private val actionMode: ArticleActionMode
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            article: Article,
            collectState: ArticleCollectState
        ) {
            binding.tvAuthor.text = article.displayAuthor()
            binding.tvTagTop.visibility = if (article.isTop) View.VISIBLE else View.GONE
            binding.tvTagCategory.text = if (!article.chapterName.isNullOrBlank()) article.chapterName else "未知"
            if (!article.superChapterName.isNullOrBlank()) {
                binding.tvCategory.text = article.superChapterName
                binding.tvCategory.visibility = View.VISIBLE
            } else {
                binding.tvCategory.visibility = View.GONE
            }
            binding.tvTitle.text = article.title.toHighlightText()
            binding.tvTime.text = buildTimeText(article)

            bindAction(
                article = article,
                collectState = collectState
            )
            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }

        fun bindAction(
            article: Article,
            collectState: ArticleCollectState
        ) {
            when (actionMode) {
                ArticleActionMode.COLLECT -> {
                    bindCollectAction(collectState.isCollected)
                    binding.btnCollect.isEnabled = !collectState.isPending
                    binding.btnCollect.alpha = if (collectState.isPending) 0.5f else 1f
                }
                ArticleActionMode.COLLECTED,
                ArticleActionMode.SHARED -> {
                    bindDeleteAction()
                    binding.btnCollect.isEnabled = true
                    binding.btnCollect.alpha = 1f
                }
            }

            binding.btnCollect.setOnClickListener {
                val actionArticle =
                    if (actionMode == ArticleActionMode.COLLECT) {
                        article.copy(collect = collectState.isCollected)
                    } else {
                        article
                    }

                onActionClick.invoke(actionArticle)
            }
        }

        private fun buildTimeText(article: Article): String {
            val time = if (!article.niceDate.isNullOrBlank()) article.niceDate else "未知"
            return when (actionMode) {
                ArticleActionMode.COLLECT -> time
                ArticleActionMode.COLLECTED -> "收藏于 $time"
                ArticleActionMode.SHARED -> "分享于 $time"
            }
        }

        private fun bindCollectAction(isCollected: Boolean) {
            binding.btnCollect.setImageResource(
                if (isCollected) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_line
            )
            binding.btnCollect.setBackgroundResource(resolveBorderlessSelectableBackground())
            binding.btnCollect.contentDescription = if (isCollected) "取消收藏" else "收藏"
        }

        private fun bindDeleteAction() {
            binding.btnCollect.setImageResource(R.drawable.ic_delete_line)
            binding.btnCollect.setBackgroundResource(R.drawable.bg_icon_collect)
            binding.btnCollect.contentDescription = "删除"
        }

        private fun resolveBorderlessSelectableBackground(): Int {
            val typedValue = TypedValue()
            binding.root.context.theme.resolveAttribute(
                android.R.attr.selectableItemBackgroundBorderless,
                typedValue,
                true
            )
            return typedValue.resourceId
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
