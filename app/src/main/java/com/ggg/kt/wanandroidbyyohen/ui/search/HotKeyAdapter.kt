package com.ggg.kt.wanandroidbyyohen.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.data.model.HotKey
import com.ggg.kt.wanandroidbyyohen.databinding.ItemHotKeyBinding

class HotKeyAdapter(
    private val onItemClick: (HotKey) -> Unit
) : RecyclerView.Adapter<HotKeyAdapter.HotKeyViewHolder>() {

    private val hotKeys = mutableListOf<HotKey>()

    fun submitList(newList: List<HotKey>) {
        hotKeys.clear()
        hotKeys.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotKeyViewHolder {
        val binding = ItemHotKeyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HotKeyViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HotKeyViewHolder, position: Int) {
        holder.bind(hotKeys[position])
    }

    override fun getItemCount(): Int = hotKeys.size

    class HotKeyViewHolder(
        private val binding: ItemHotKeyBinding,
        private val onItemClick: (HotKey) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HotKey) {
            binding.tvHotKey.text = item.name

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}