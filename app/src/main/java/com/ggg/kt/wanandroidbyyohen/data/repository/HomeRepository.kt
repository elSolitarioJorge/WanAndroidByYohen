package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.data.model.HomeData

class HomeRepository {
    suspend fun refreshHomeData(): UiState<HomeData> {
        return try {
            val bannerResponse = RetrofitClient.api.getBanners()
            val topResponse = RetrofitClient.api.getTopArticles()
            val articleResponse = RetrofitClient.api.getHomeArticles(page = 0)

            if (bannerResponse.errorCode != 0) {
                return UiState.Error(bannerResponse.errorMsg.ifBlank { "Banner 请求失败" })
            }

            if (topResponse.errorCode != 0) {
                return UiState.Error(topResponse.errorMsg.ifBlank { "置顶文章请求失败" })
            }

            if (articleResponse.errorCode != 0) {
                return UiState.Error(articleResponse.errorMsg.ifBlank { "首页文章请求失败" })
            }

            val banners = bannerResponse.data.orEmpty()
            val topArticles = topResponse.data.orEmpty().map {
                it.copy(isTop = true)
            }

            val pageData = articleResponse.data
            val normalArticles = pageData?.datas.orEmpty()

            UiState.Success(
                HomeData(
                    banners = banners,
                    articles = topArticles + normalArticles,
                    isRefresh = true,
                    hasMore = pageData?.over != true
                )
            )

        } catch(e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun loadMoreArticles(page: Int): UiState<HomeData> {
        return try {
            val articleResponse = RetrofitClient.api.getHomeArticles(page)
            if (articleResponse.errorCode != 0) {
                return UiState.Error(articleResponse.errorMsg.ifBlank { "加载更多失败" })
            }

            val pageData = articleResponse.data

            UiState.Success(
                HomeData(
                    banners = emptyList(),
                    articles = pageData?.datas.orEmpty(),
                    isRefresh = false,
                    hasMore = pageData?.over != true
                )
            )
        } catch(e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }
}