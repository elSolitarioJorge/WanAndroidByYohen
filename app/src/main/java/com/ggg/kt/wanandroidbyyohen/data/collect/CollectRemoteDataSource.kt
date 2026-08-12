package com.ggg.kt.wanandroidbyyohen.data.collect

import com.ggg.kt.wanandroidbyyohen.common.base.UiState

interface CollectRemoteDataSource {
    suspend fun setCollected(
        articleId: Int,
        isCollected: Boolean
    ): UiState<Any>
}