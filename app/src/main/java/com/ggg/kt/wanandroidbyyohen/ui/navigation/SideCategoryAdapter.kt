package com.ggg.kt.wanandroidbyyohen.ui.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.databinding.ItemNavigationCategoryBinding

class SideCategoryAdapter<T>(
    private val getTitle: (T) -> String,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SideCategoryAdapter<T>.CategoryViewHolder>() {
    private val items = mutableListOf<T>()
    private var selectedPosition = 0

    fun submitList(
        newList: List<T>,
        selectedPosition: Int = this.selectedPosition
    ) {
        items.clear()
        items.addAll(newList)
        this.selectedPosition = if (items.isEmpty()) {
            0
        } else {
            selectedPosition.coerceIn(items.indices)
        }
        notifyDataSetChanged()
    }

    fun select(position: Int) {
        if (position !in items.indices) return
        if (position == selectedPosition) return

        val oldPosition = selectedPosition
        selectedPosition = position
        if (oldPosition in items.indices) {
            notifyItemChanged(oldPosition)
        }
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
            binding.root.isSelected = selected
            binding.tvCategory.isSelected = selected
            binding.viewIndicator.visibility = if (selected) View.VISIBLE else View.INVISIBLE

            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
        }
    }

}
