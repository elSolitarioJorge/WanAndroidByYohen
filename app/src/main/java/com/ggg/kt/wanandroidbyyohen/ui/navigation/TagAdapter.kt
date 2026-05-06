package com.ggg.kt.wanandroidbyyohen.ui.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.databinding.ItemTagBinding
import com.google.android.flexbox.FlexboxLayoutManager

class TagAdapter<T>(
    private val getName: (T) -> String,
    private val onItemClick: (T) -> Unit
) : RecyclerView.Adapter<TagAdapter<T>.TagViewHolder>() {
    private val items = mutableListOf<T>()

    fun submitList(newList: List<T>) {
        if (items == newList) return

        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemTagBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TagViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class TagViewHolder(
        private val binding: ItemTagBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T) {
            binding.tvTag.text = getName(item)
            applyFlexGrow(binding.root)
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }
    }

    private fun applyFlexGrow(view: View) {
        val layoutParams = view.layoutParams
        if (layoutParams is FlexboxLayoutManager.LayoutParams) {
            layoutParams.flexGrow = 1f
            view.layoutParams = layoutParams
        }
    }
}
