package com.ggg.kt.wanandroidbyyohen.ui.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.R
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
        val safeSelectedPosition = if (newList.isEmpty()) {
            0
        } else {
            selectedPosition.coerceIn(newList.indices)
        }

        if (items == newList) {
            select(safeSelectedPosition)
            return
        }

        items.clear()
        items.addAll(newList)
        this.selectedPosition = safeSelectedPosition
        notifyDataSetChanged()
    }

    fun select(position: Int) {
        select(position, recyclerView = null)
    }

    fun select(
        position: Int,
        recyclerView: RecyclerView?
    ) {
        if (position !in items.indices) return
        if (position == selectedPosition) return

        val oldPosition = selectedPosition
        selectedPosition = position

        if (recyclerView != null) {
            updateVisibleSelection(recyclerView, oldPosition, selected = false)
            updateVisibleSelection(recyclerView, selectedPosition, selected = true)
        } else if (oldPosition in items.indices) {
            notifyItemChanged(oldPosition, PAYLOAD_SELECTION)
            notifyItemChanged(selectedPosition, PAYLOAD_SELECTION)
        }
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

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.contains(PAYLOAD_SELECTION)) {
            holder.bindSelected(position == selectedPosition)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class CategoryViewHolder(
        private val binding: ItemNavigationCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T, selected: Boolean) {
            binding.tvCategory.text = getTitle(item)
            bindSelected(selected)

            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
        }

        fun bindSelected(selected: Boolean) {
            binding.root.isSelected = selected
            binding.tvCategory.isSelected = selected
            binding.viewIndicator.visibility = if (selected) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun updateVisibleSelection(
        recyclerView: RecyclerView,
        position: Int,
        selected: Boolean
    ) {
        val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
            ?: return
        itemView.isSelected = selected
        itemView.findViewById<TextView>(R.id.tv_category)?.isSelected = selected
        itemView.findViewById<View>(R.id.view_indicator)?.visibility =
            if (selected) View.VISIBLE else View.INVISIBLE
    }

    private companion object {
        const val PAYLOAD_SELECTION = "payload_selection"
    }
}
