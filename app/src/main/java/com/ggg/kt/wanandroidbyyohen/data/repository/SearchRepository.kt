package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.data.model.HotKey
import com.ggg.kt.wanandroidbyyohen.data.model.SearchArticleData

class SearchRepository {
    suspend fun getHotKeys(): UiState<List<HotKey>> {
        return try {
            val response = RetrofitClient.api.getHotKeys()
            if (response.errorCode == 0) {
                UiState.Success(response.data.orEmpty())
            } else {
                UiState.Error(response.errorMsg.ifBlank { "热搜词请求失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun searchArticles(
        page: Int,
        keyword: String,
        isRefresh: Boolean
    ): UiState<SearchArticleData> {
        return try {
            val response = RetrofitClient.api.searchArticles(
                page = page,
                keyword = keyword
            )

            if (response.errorCode != 0) {
                return UiState.Error(response.errorMsg.ifBlank { "搜索失败" })
            }

            val pageData = response.data

            UiState.Success(
                SearchArticleData(
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