package com.ggg.kt.wanandroidbyyohen.ui.square

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.data.model.SquareTag
import com.ggg.kt.wanandroidbyyohen.databinding.ItemSquareTagBinding

class SquareTagAdapter(
    private val onTagClick: (SquareTag) -> Unit
) : RecyclerView.Adapter<SquareTagAdapter.SquareTagViewHolder>() {

    private val tags = mutableListOf<SquareTag>()
    private var selectedPosition = 0

    fun submitList(newList: List<SquareTag>) {
        tags.clear()
        tags.addAll(newList)
        selectedPosition = 0
        notifyDataSetChanged()
    }

    fun selectTag(tag: SquareTag) {
        val newPosition = tags.indexOfFirst { it.key == tag.key }
        if (newPosition == -1 || newPosition == selectedPosition) return

        val oldPosition = selectedPosition
        selectedPosition = newPosition
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquareTagViewHolder {
        val binding = ItemSquareTagBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SquareTagViewHolder(binding, onTagClick)
    }

    override fun onBindViewHolder(holder: SquareTagViewHolder, position: Int) {
        holder.bind(tags[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = tags.size

    class SquareTagViewHolder(
        private val binding: ItemSquareTagBinding,
        private val onTagClick: (SquareTag) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: SquareTag, selected: Boolean) {
            binding.tvSquareTag.text = tag.title
            binding.tvSquareTag.isSelected = selected
            binding.root.setOnClickListener {
                onTagClick(tag)
            }
        }
    }
}
