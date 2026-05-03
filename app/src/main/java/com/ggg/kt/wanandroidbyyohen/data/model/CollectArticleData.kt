package com.ggg.kt.wanandroidbyyohen.data.model

data class CollectArticleData(
    val articles: List<Article>,
    val isRefresh: Boolean,
    val hasMore: Boolean
)
