package com.ggg.kt.wanandroidbyyohen.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.data.model.HotKey
import com.ggg.kt.wanandroidbyyohen.databinding.ItemHotKeyBinding
import com.google.android.flexbox.FlexboxLayoutManager

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

    inner class HotKeyViewHolder(
        private val binding: ItemHotKeyBinding,
        private val onItemClick: (HotKey) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HotKey) {
            binding.tvHotKey.text = item.name
            applyFlexGrow(binding.root)
            binding.root.setOnClickListener {
                onItemClick(item)
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