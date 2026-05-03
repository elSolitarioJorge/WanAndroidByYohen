package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCall
import com.ggg.kt.wanandroidbyyohen.data.model.HotKey
import com.ggg.kt.wanandroidbyyohen.data.model.SearchArticleData

class SearchRepository {
    suspend fun getHotKeys(): UiState<List<HotKey>> {
        return safeApiCall(
            defaultErrorMessage = "热搜词请求失败"
        ) {
            RetrofitClient.api.getHotKeys()
        }
    }

    suspend fun searchArticles(
        page: Int,
        keyword: String,
        isRefresh: Boolean
    ): UiState<SearchArticleData> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "搜索失败"
            ) {
                RetrofitClient.api.searchArticles(
                    page = page,
                    keyword = keyword
                )
            }
        ) {
            is UiState.Success -> {
                val pageData = result.data

                UiState.Success(
                    SearchArticleData(
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
