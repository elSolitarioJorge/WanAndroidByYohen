package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.data.model.MyShareArticleData

class ShareRepository {
    suspend fun shareArticle(
        title: String,
        link: String
    ): UiState<Any> {
        return try {
            var response = RetrofitClient.api.shareArticle(
                title = title,
                link = link
            )
            if (response.errorCode == 0) {
                UiState.Success(Any())
            } else {
                UiState.Error(response.errorMsg.ifBlank { "分享文章失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun getMyShareArticles(
        page: Int,
        isRefresh: Boolean
    ): UiState<MyShareArticleData> {
        return try {
            val response = RetrofitClient.api.getMyShareArticles(page)

            if (response.errorCode != 0) {
                return UiState.Error(response.errorMsg.ifBlank { "我的分享请求失败" })
            }

            val pageData = response.data?.shareArticles

            UiState.Success(
                MyShareArticleData(
                    articles = pageData?.datas.orEmpty(),
                    isRefresh = isRefresh,
                    hasMore = pageData?.over != true
                )
            )
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun deleteMyShareArticle(id: Int): UiState<Any> {
        return try {
            val response = RetrofitClient.api.deleteMyShareArticle(id)

            if (response.errorCode == 0) {
                UiState.Success(Any())
            } else {
                UiState.Error(response.errorMsg.ifBlank { "删除分享失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }
}