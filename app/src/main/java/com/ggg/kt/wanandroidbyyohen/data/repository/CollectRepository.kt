package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient

class CollectRepository {

    suspend fun collectArticle(id: Int): UiState<Any> {
        return try {
            val response = RetrofitClient.api.collectArticle(id)

            if (response.errorCode == 0) {
                UiState.Success(Any())
            } else {
                UiState.Error(response.errorMsg.ifBlank { "收藏失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun uncollectArticle(id: Int): UiState<Any> {
        return try {
            val response = RetrofitClient.api.uncollectArticle(id)

            if (response.errorCode == 0) {
                UiState.Success(Any())
            } else {
                UiState.Error(response.errorMsg.ifBlank { "取消收藏失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }
}