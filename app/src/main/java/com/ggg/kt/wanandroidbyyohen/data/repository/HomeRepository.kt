package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCall
import com.ggg.kt.wanandroidbyyohen.data.model.HomeData

class HomeRepository {
    suspend fun refreshHomeData(): UiState<HomeData> {
        val bannerResult = safeApiCall(
            defaultErrorMessage = "Banner 请求失败"
        ) {
            RetrofitClient.api.getBanners()
        }
        val banners = when (bannerResult) {
            is UiState.Success -> bannerResult.data
            is UiState.Error -> return UiState.Error(bannerResult.message)
            is UiState.Loading -> return UiState.Loading
        }

        val topResult = safeApiCall(
            defaultErrorMessage = "置顶文章请求失败"
        ) {
            RetrofitClient.api.getTopArticles()
        }
        val topArticles = when (topResult) {
            is UiState.Success -> topResult.data.map {
                it.copy(isTop = true)
            }
            is UiState.Error -> return UiState.Error(topResult.message)
            is UiState.Loading -> return UiState.Loading
        }

        val articleResult = safeApiCall(
            defaultErrorMessage = "首页文章请求失败"
        ) {
            RetrofitClient.api.getHomeArticles(page = 0)
        }
        return when (articleResult) {
            is UiState.Success -> {
                val pageData = articleResult.data
                val normalArticles = pageData.datas

                UiState.Success(
                    HomeData(
                        banners = banners,
                        articles = topArticles + normalArticles,
                        isRefresh = true,
                        hasMore = !pageData.over
                    )
                )
            }

            is UiState.Error -> UiState.Error(articleResult.message)

            is UiState.Loading -> UiState.Loading
        }
    }

    suspend fun loadMoreArticles(page: Int): UiState<HomeData> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "加载更多失败"
            ) {
                RetrofitClient.api.getHomeArticles(page)
            }
        ) {
            is UiState.Success -> {
                val pageData = result.data

                UiState.Success(
                    HomeData(
                        banners = emptyList(),
                        articles = pageData.datas,
                        isRefresh = false,
                        hasMore = !pageData.over
                    )
                )
            }

            is UiState.Error -> UiState.Error(result.message)

            is UiState.Loading -> UiState.Loading
        }
    }
}
