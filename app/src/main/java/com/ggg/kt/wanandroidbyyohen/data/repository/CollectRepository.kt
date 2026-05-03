package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCall
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCallWithoutData
import com.ggg.kt.wanandroidbyyohen.data.model.CollectArticleData

class CollectRepository {

    suspend fun collectArticle(id: Int): UiState<Any> {
        return safeApiCallWithoutData(
            defaultErrorMessage = "收藏失败"
        ) {
            RetrofitClient.api.collectArticle(id)
        }
    }

    suspend fun uncollectArticle(id: Int): UiState<Any> {
        return safeApiCallWithoutData(
            defaultErrorMessage = "取消收藏失败"
        ) {
            RetrofitClient.api.uncollectArticle(id)
        }
    }

    suspend fun getCollectArticles(
        page: Int,
        isRefresh: Boolean
    ): UiState<CollectArticleData> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "收藏列表请求失败"
            ) {
                RetrofitClient.api.getCollectArticles(page)
            }
        ) {
            is UiState.Success -> {
                UiState.Success(
                    CollectArticleData(
                        articles = result.data.datas,
                        isRefresh = isRefresh,
                        hasMore = !result.data.over
                    )
                )
            }

            is UiState.Error -> UiState.Error(result.message)

            is UiState.Loading -> UiState.Loading
        }
    }

    suspend fun uncollectArticleFromMine(
        id: Int,
        originId: Int
    ): UiState<Any> {
        return safeApiCallWithoutData(
            defaultErrorMessage = "取消收藏失败"
        ) {
            RetrofitClient.api.uncollectArticleFromMine(
                id = id,
                originId = originId
            )
        }
    }
}