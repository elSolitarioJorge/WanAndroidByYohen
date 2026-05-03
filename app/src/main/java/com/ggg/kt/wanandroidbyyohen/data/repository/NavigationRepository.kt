package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCall
import com.ggg.kt.wanandroidbyyohen.data.model.Chapter
import com.ggg.kt.wanandroidbyyohen.data.model.Navigation
import com.ggg.kt.wanandroidbyyohen.data.model.SystemArticleData

class NavigationRepository {
    suspend fun getNavigationList(): UiState<List<Navigation>> {
        return safeApiCall(
            defaultErrorMessage = "导航数据请求失败"
        ) {
            RetrofitClient.api.getNavigationList()
        }
    }

    suspend fun getSystemTree(): UiState<List<Chapter>> {
        return safeApiCall(
            defaultErrorMessage = "体系数据请求失败"
        ) {
            RetrofitClient.api.getSystemTree()
        }
    }

    suspend fun getSystemArticles(
        page: Int,
        cid: Int,
        isRefresh: Boolean
    ): UiState<SystemArticleData> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "体系文章请求失败"
            ) {
                RetrofitClient.api.getArticleByCid(page, cid)
            }
        ) {
            is UiState.Success -> {
                val pageData = result.data

                UiState.Success(
                    SystemArticleData(
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
