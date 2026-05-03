package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.data.model.CollectArticleData

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

    suspend fun getCollectArticles(
        page: Int,
        isRefresh: Boolean
    ): UiState<CollectArticleData> {
        return try {
            val response = RetrofitClient.api.getCollectArticles(page)
            if (response.errorCode != 0) {
                return UiState.Error(response.errorMsg.ifBlank { "收藏列表请求失败" })
            }

            val pageData = response.data

            UiState.Success(
                CollectArticleData(
                    articles = pageData?.datas.orEmpty(),
                    isRefresh = isRefresh,
                    hasMore = pageData?.over != true
                )
            )
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun uncollectArticleFromMine(
        id: Int,
        originId: Int
    ): UiState<Any> {
        return try {
            val response = RetrofitClient.api.uncollectArticleFromMine(
                id = id,
                originId = originId
            )
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