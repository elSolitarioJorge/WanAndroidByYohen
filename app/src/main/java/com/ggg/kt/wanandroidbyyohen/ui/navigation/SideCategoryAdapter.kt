package com.ggg.kt.wanandroidbyyohen.ui.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.databinding.ItemNavigationCategoryBinding

class SideCategoryAdapter<T>(
    private val getTitle: (T) -> String,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SideCategoryAdapter<T>.CategoryViewHolder>() {
    private val items = mutableListOf<T>()
    private var selectedPosition = 0

    fun submitList(newList: List<T>) {
        items.clear()
        items.addAll(newList)
        selectedPosition = 0
        notifyDataSetChanged()
    }

    fun select(position: Int) {
        if (position !in items.indices) return

        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedPosition)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val binding = ItemNavigationCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = items.size

    inner class CategoryViewHolder(
        private val binding: ItemNavigationCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T, selected: Boolean) {
            binding.tvCategory.text = getTitle(item)
            binding.tvCategory.setTextColor(
                if (selected) 0xFF3B82F6.toInt() else 0xFF666666.toInt()
            )

            binding.root.setBackgroundColor(
                if (selected) 0xFFFFFFFF.toInt() else 0xFFF7F7F7.toInt()
            )

            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
        }
    }

}