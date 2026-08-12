package com.ggg.kt.wanandroidbyyohen.ui.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.collect.ArticleCollectProvider
import com.ggg.kt.wanandroidbyyohen.data.collect.ArticleCollectState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.CollectArticleData
import com.ggg.kt.wanandroidbyyohen.data.repository.CollectRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CollectArticleViewModel : ViewModel() {
    private val repository = CollectRepository()
    private val _collectArticleState =
        MutableStateFlow<UiState<CollectArticleData>>(UiState.Loading)
    val collectArticleState: StateFlow<UiState<CollectArticleData>> = _collectArticleState

    private val articleCollectRepository = ArticleCollectProvider.repository

    val collectStates: StateFlow<Map<Int, ArticleCollectState>> = articleCollectRepository.states

    private val _uncollectMessages =
        MutableSharedFlow<String>(
            extraBufferCapacity = 1
        )

    val uncollectMessages: SharedFlow<String> =
        _uncollectMessages.asSharedFlow()

    private var currentPage = 0
    private var hasMore = true
    private var isLoadingMore = false
    private var currentData: CollectArticleData? = null

    fun refresh() {
        viewModelScope.launch {
            currentPage = 0
            hasMore = true
            isLoadingMore = false
            currentData = null

            _collectArticleState.value = UiState.Loading

            val result = repository.getCollectArticles(
                page = currentPage,
                isRefresh = true
            )

            if (result is UiState.Success) {
                articleCollectRepository.seedCollectedArticles(
                    result.data.articles
                )
                currentData = result.data
                hasMore = result.data.hasMore
                currentPage++
                _collectArticleState.value = UiState.Success(result.data)
            } else {
                _collectArticleState.value = result
            }
        }
    }

    fun loadMore() {
        if (isLoadingMore || !hasMore) return

        viewModelScope.launch {
            isLoadingMore = true

            val result = repository.getCollectArticles(
                page = currentPage,
                isRefresh = false
            )

            if (result is UiState.Success) {
                articleCollectRepository.seedCollectedArticles(
                    result.data.articles
                )
                val oldData = currentData
                val mergedData = oldData?.copy(
                    articles = oldData.articles + result.data.articles,
                    isRefresh = false,
                    hasMore = result.data.hasMore
                ) ?: result.data
                currentData = mergedData
                hasMore = result.data.hasMore
                currentPage++
                _collectArticleState.value = UiState.Success(mergedData)
            } else {
                _collectArticleState.value = result
            }

            isLoadingMore = false
        }
    }

    fun uncollect(article: Article) {
        viewModelScope.launch {
            when (
                val result =
                    articleCollectRepository.uncollectFromMine(
                        collectionId = article.id,
                        originId = article.originId
                    )
            ) {
                is UiState.Success -> {
                    val updatedData = currentData?.copy(
                        articles =
                            currentData
                                ?.articles
                                .orEmpty()
                                .filter {
                                    it.id != article.id
                                }
                    )

                    currentData = updatedData

                    updatedData?.let {
                        _collectArticleState.value = UiState.Success(it)
                    }

                    _uncollectMessages.emit(
                        "已取消收藏"
                    )
                }

                is UiState.Error -> {
                    _uncollectMessages.emit(
                        result.message
                    )
                }

                else -> Unit
            }
        }
    }
}
