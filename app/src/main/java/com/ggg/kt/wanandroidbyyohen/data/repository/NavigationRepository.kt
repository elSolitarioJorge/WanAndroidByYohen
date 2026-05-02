package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.data.model.Chapter
import com.ggg.kt.wanandroidbyyohen.data.model.Navigation
import com.ggg.kt.wanandroidbyyohen.data.model.SystemArticleData

class NavigationRepository {
    suspend fun getNavigationList(): UiState<List<Navigation>> {
        return try {
            val response = RetrofitClient.api.getNavigationList()

            if (response.errorCode == 0) {
                UiState.Success(response.data.orEmpty())
            } else {
                UiState.Error(response.errorMsg.ifBlank { "导航数据请求失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun getSystemTree(): UiState<List<Chapter>> {
        return try {
            val response = RetrofitClient.api.getSystemTree()
            if (response.errorCode == 0) {
                UiState.Success(response.data.orEmpty())
            } else {
                UiState.Error(response.errorMsg.ifBlank { "体系数据请求失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun getSystemArticles(
        page: Int,
        cid: Int,
        isRefresh: Boolean
    ): UiState<SystemArticleData> {
        return try {
            val response = RetrofitClient.api.getArticleByCid(page, cid)
            if (response.errorCode != 0) {
                 return UiState.Error(response.errorMsg.ifBlank { "体系文章请求失败" })
            }

            val pageData = response.data

            UiState.Success(
                SystemArticleData(
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