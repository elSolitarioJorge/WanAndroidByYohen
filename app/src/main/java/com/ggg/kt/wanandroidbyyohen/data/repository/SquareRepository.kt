package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.data.model.SquareData

class SquareRepository {
    suspend fun getSquareArticles(
        page: Int,
        isRefresh: Boolean
    ): UiState<SquareData> {
        return try {
            val response = RetrofitClient.api.getSquareArticles(page)
            if (response.errorCode != 0) {
                return UiState.Error(response.errorMsg.ifBlank { "广场文章请求失败" })
            }
            val pageData = response.data
            UiState.Success(
                SquareData(
                    articles = pageData?.datas.orEmpty(),
                    isRefresh = isRefresh,
                    hasMore = pageData?.over != true
                )
            )

        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }
}