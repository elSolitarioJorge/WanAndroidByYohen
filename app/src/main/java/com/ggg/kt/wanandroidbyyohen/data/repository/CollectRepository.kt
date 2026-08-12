package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCall
import com.ggg.kt.wanandroidbyyohen.data.model.CollectArticleData

class CollectRepository {

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
}
