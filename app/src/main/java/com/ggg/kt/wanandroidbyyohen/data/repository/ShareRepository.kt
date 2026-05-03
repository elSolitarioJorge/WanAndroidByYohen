package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCall
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCallWithoutData
import com.ggg.kt.wanandroidbyyohen.data.model.MyShareArticleData

class ShareRepository {
    suspend fun shareArticle(
        title: String,
        link: String
    ): UiState<Any> {
        return safeApiCallWithoutData(
            defaultErrorMessage = "分享文章失败"
        ) {
            RetrofitClient.api.shareArticle(title, link)
        }
    }

    suspend fun getMyShareArticles(
        page: Int,
        isRefresh: Boolean
    ): UiState<MyShareArticleData> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "我的分享请求失败"
            ) {
                RetrofitClient.api.getMyShareArticles(page)
            }
        ) {
            is UiState.Success -> {
                val pageData = result.data.shareArticles

                UiState.Success(
                    MyShareArticleData(
                        articles = pageData?.datas.orEmpty(),
                        isRefresh = isRefresh,
                        hasMore = pageData?.over != true
                    )
                )
            }

            is UiState.Error -> UiState.Error(result.message)

            is UiState.Loading -> UiState.Loading
        }
    }

    suspend fun deleteMyShareArticle(id: Int): UiState<Any> {
        return safeApiCallWithoutData(
            defaultErrorMessage = "删除分享失败"
        ) {
            RetrofitClient.api.deleteMyShareArticle(id)
        }
    }
}