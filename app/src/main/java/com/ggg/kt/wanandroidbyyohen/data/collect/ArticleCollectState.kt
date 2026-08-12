package com.ggg.kt.wanandroidbyyohen.data.collect

data class ArticleCollectState(
    // 当前页面应该显示收藏还是未收藏
    val isCollected: Boolean,
    // 该文章是否正在发送收藏请求，用于阻止连续点击产生并发错乱
    val isPending: Boolean = false
)
