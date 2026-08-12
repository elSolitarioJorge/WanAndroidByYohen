package com.ggg.kt.wanandroidbyyohen.data.collect

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCallWithoutData

class NetworkCollectRemoteDataSource : CollectRemoteDataSource {

    override suspend fun setCollected(
        articleId: Int,
        isCollected: Boolean
    ): UiState<Any> {
        return if (isCollected) {
            safeApiCallWithoutData(
                defaultErrorMessage = "收藏失败"
            ) {
                RetrofitClient.api.collectArticle(articleId)
            }
        } else {
            safeApiCallWithoutData(
                defaultErrorMessage = "取消收藏失败"
            ) {
                RetrofitClient.api.uncollectArticle(articleId)
            }
        }
    }

    override suspend fun uncollectFromMine(
        collectionId: Int,
        originId: Int
    ): UiState<Any> {
        return safeApiCallWithoutData(
            defaultErrorMessage = "取消收藏失败"
        ) {
            RetrofitClient.api.uncollectArticleFromMine(
                id = collectionId,
                originId = originId
            )
        }
    }
}