package com.ggg.kt.wanandroidbyyohen.data.model

data class SearchArticleData(
    val articles: List<Article>,
    val isRefresh: Boolean,
    val hasMore: Boolean
)
