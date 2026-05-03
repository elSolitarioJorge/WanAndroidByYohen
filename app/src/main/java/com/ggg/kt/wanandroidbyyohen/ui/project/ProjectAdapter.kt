package com.ggg.kt.wanandroidbyyohen.ui.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.databinding.ItemProjectBinding

class ProjectAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onCollectClick: ((Article) -> Unit)? = null
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private val projects = mutableListOf<Article>()

    fun submitList(newList: List<Article>) {
        projects.clear()
        projects.addAll(newList)
        notifyDataSetChanged()
    }

    fun addList(newList: List<Article>) {
        if (newList.isEmpty()) return

        val startPosition = projects.size
        projects.addAll(newList)
        notifyItemRangeInserted(startPosition, newList.size)
    }

    fun updateCollectState(articleId: Int, collect: Boolean) {
        val index = projects.indexOfFirst { it.id == articleId }
        if (index == -1) return

        projects[index] = projects[index].copy(collect = collect)
        notifyItemChanged(index)
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
        holder.bind(projects[position])
    }

    override fun getItemCount(): Int = projects.size

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

            binding.tvCollect.text = if (article.collect) "♥" else "♡"
            binding.tvCollect.setTextColor(
                if (article.collect) {
                    0xFFE91E63.toInt()
                } else {
                    0xFF999999.toInt()
                }
            )

            binding.root.setOnClickListener {
                onItemClick(article)
            }

            binding.tvCollect.setOnClickListener {
                onCollectClick?.invoke(article)
            }
        }
    }
}