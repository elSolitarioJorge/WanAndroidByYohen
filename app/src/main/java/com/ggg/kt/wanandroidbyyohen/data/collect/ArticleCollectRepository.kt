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

    fun seedCollectedArticles(
        articles: List<Article>
    ) {
        store.seed(
            articles.associate { article ->
                val sourceArticleId =
                    article.originId.takeIf { it > 0 }
                        ?: article.id

                sourceArticleId to true
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

        return executeUpdate(
            articleId = articleId,
            targetCollected = targetCollected
        ) {
            remoteDataSource.setCollected(
                articleId = articleId,
                isCollected = targetCollected
            )
        }
    }

    suspend fun uncollectFromMine(
        collectionId: Int,
        originId: Int
    ): UiState<Boolean>? {
        val sourceArticleId =
            originId.takeIf { it > 0 }
                ?: collectionId

        val accepted = store.beginSet(
            articleId = sourceArticleId,
            fallbackCollected = true,
            targetCollected = false
        )

        if (!accepted) {
            return null
        }

        return executeUpdate(
            articleId = sourceArticleId,
            targetCollected = false
        ) {
            remoteDataSource.uncollectFromMine(
                collectionId = collectionId,
                originId = originId
            )
        }
    }

    private suspend fun executeUpdate(
        articleId: Int,
        targetCollected: Boolean,
        request: suspend () -> UiState<Any>
    ): UiState<Boolean> {
        return try {
            when (val result = request()) {
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
        } catch (exception: CancellationException) {
            store.rollback(articleId)
            throw exception
        } catch (exception: Exception) {
            store.rollback(articleId)

            UiState.Error(
                exception.message ?: "收藏失败"
            )
        }
    }

    fun clear() {
        store.clear()
    }
}