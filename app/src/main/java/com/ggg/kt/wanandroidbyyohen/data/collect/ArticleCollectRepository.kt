package com.ggg.kt.wanandroidbyyohen.data.collect

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.cancellation.CancellationException

class ArticleCollectRepository(
    private val store: ArticleCollectStore,
    private val remoteDataSource: CollectRemoteDataSource
) {
    val states: StateFlow<Map<Int, ArticleCollectState>> =
        store.states

    fun seed(articles: List<Article>) {
        store.seed(
            articles.associate { article ->
                article.id to article.collect
            }
        )
    }

    suspend fun toggle(
        articleId: Int,
        fallbackCollected: Boolean
    ): UiState<Boolean>? {
        val targetCollected = store.beginToggle(
            articleId = articleId,
            fallbackCollected = fallbackCollected
        ) ?: return null

        return try {
            when (
                val result = remoteDataSource.setCollected(
                    articleId = articleId,
                    isCollected = targetCollected
                )
            ) {
                is UiState.Success -> {
                    store.confirm(articleId)
                    UiState.Success(targetCollected)
                }

                is UiState.Error -> {
                    store.rollback(articleId)
                    UiState.Error(result.message)
                }

                is UiState.Loading -> {
                    store.rollback(articleId)
                    UiState.Error("收藏请求未完成")
                }
            }
        } catch (e: CancellationException) {
            store.rollback(articleId)
            throw e
        } catch (e: Exception) {
            store.rollback(articleId)
            UiState.Error(e.message ?: "收藏失败")
        }
    }

    fun clear() {
        store.clear()
    }
}