package com.ggg.kt.wanandroidbyyohen.ui.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.databinding.ItemSectionBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class SectionAdapter<T>(
    private val getItemName: (T) -> String,
    private val onItemClick: (T) -> Unit
) : RecyclerView.Adapter<SectionAdapter<T>.SectionViewHolder>() {

    private val sections = mutableListOf<SectionUi<T>>()

    fun submitList(newList: List<SectionUi<T>>) {
        if (sections == newList) return

        sections.clear()
        sections.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val binding = ItemSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(sections[position])
    }

    override fun getItemCount(): Int = sections.size

    inner class SectionViewHolder(
        private val binding: ItemSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val tagAdapter = TagAdapter(
            getName = getItemName,
            onItemClick = onItemClick
        )

        init {
            binding.rvTags.layoutManager = FlexboxLayoutManager(
                binding.root.context
            ).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            binding.rvTags.adapter = tagAdapter
        }

        fun bind(section: SectionUi<T>) {
            binding.tvSectionTitle.text = section.title
            tagAdapter.submitList(section.items)
        }

    }
}
