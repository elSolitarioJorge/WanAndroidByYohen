package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCall
import com.ggg.kt.wanandroidbyyohen.data.model.SquareData

class SquareRepository {
    suspend fun getSquareArticles(
        page: Int,
        isRefresh: Boolean
    ): UiState<SquareData> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "广场文章请求失败"
            ) {
                RetrofitClient.api.getSquareArticles(page)
            }
        ) {
            is UiState.Success -> {
                val pageData = result.data
                UiState.Success(
                    SquareData(
                        articles = pageData.datas,
                        isRefresh = isRefresh,
                        hasMore = !pageData.over
                    )
                )
            }

            is UiState.Error -> UiState.Error(result.message)

            is UiState.Loading -> UiState.Loading
        }
    }
}
