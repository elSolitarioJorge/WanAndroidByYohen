package com.ggg.kt.wanandroidbyyohen.data.model

data class HomeData(
    val banners: List<Banner>,
    val articles: List<Article>,
    val isRefresh: Boolean = true,
    val hasMore: Boolean = true
)
