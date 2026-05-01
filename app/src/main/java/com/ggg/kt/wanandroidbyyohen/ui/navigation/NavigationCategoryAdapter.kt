package com.ggg.kt.wanandroidbyyohen.ui.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.data.model.Navigation
import com.ggg.kt.wanandroidbyyohen.databinding.ItemNavigationCategoryBinding

class NavigationCategoryAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<NavigationCategoryAdapter.CategoryViewHolder>() {
    private val categories = mutableListOf<Navigation>()
    private var selectedPosition = 0

    fun submitList(newList: List<Navigation>) {
        categories.clear()
        categories.addAll(newList)
        selectedPosition = 0
        notifyDataSetChanged()
    }

    fun select(position: Int) {
        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemNavigationCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = categories.size


    class CategoryViewHolder(
        private val binding: ItemNavigationCategoryBinding,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Navigation, selected: Boolean) {
            binding.tvCategory.text = item.name
            binding.tvCategory.setTextColor(
                if (selected) 0xFF3B82F6.toInt() else 0xFF666666.toInt()
            )

            binding.root.setBackgroundColor(
                if (selected) 0xFFFFFFFF.toInt() else 0xFFF7F7F7.toInt()
            )

            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }
    }
}