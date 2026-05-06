package com.ggg.kt.wanandroidbyyohen.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import com.ggg.kt.wanandroidbyyohen.data.model.Banner
import com.ggg.kt.wanandroidbyyohen.databinding.ItemHomeBannerBinding

class BannerAdapter(
    private val onBannerClick: (Banner) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    private val banners = mutableListOf<Banner>()

    val realItemCount: Int
        get() = banners.size

    fun submitList(newList: List<Banner>) {
        banners.clear()
        banners.addAll(newList)
        notifyDataSetChanged()
    }

    fun getInitialPosition(): Int {
        if (banners.size <= 1) return 0

        val middle = Int.MAX_VALUE / 2
        return middle - middle % banners.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemHomeBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerViewHolder(binding, onBannerClick)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        if (banners.isEmpty()) return

        holder.bind(banners[position % banners.size])
    }

    override fun getItemCount(): Int {
        return when (banners.size) {
            0 -> 0
            1 -> 1
            else -> Int.MAX_VALUE
        }
    }

    class BannerViewHolder(
        private val binding: ItemHomeBannerBinding,
        private val onBannerClick: (Banner) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(banner: Banner) {
            binding.ivBanner.load(banner.imagePath) {
                crossfade(true)
                error(android.R.drawable.ic_menu_report_image)
            }

            binding.root.setOnClickListener {
                onBannerClick(banner)
            }
        }
    }
}
