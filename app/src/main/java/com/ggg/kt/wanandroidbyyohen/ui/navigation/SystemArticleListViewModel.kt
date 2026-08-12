package com.ggg.kt.wanandroidbyyohen.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.collect.ArticleCollectProvider
import com.ggg.kt.wanandroidbyyohen.data.collect.ArticleCollectState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.SystemArticleData
import com.ggg.kt.wanandroidbyyohen.data.repository.NavigationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SystemArticleListViewModel : ViewModel() {
    private val repository = NavigationRepository()

    private val articleCollectRepository = ArticleCollectProvider.repository

    val collectStates: StateFlow<Map<Int, ArticleCollectState>> = articleCollectRepository.states

    private val _collectErrors = MutableSharedFlow<String>(extraBufferCapacity = 1)

    val collectErrors: SharedFlow<String> = _collectErrors.asSharedFlow()
    private val _articleState =
        MutableStateFlow<UiState<SystemArticleData>>(UiState.Loading)
    val articleState: StateFlow<UiState<SystemArticleData>> = _articleState
    private var cid: Int = -1
    private var currentPage = 0
    private var hasMore = true
    private var isLoadingMore = false
    private var currentData: SystemArticleData? = null

    fun setCid(categoryId: Int) {
        if (cid == -1) {
            cid = categoryId
        }
    }

    fun refresh() {
        if (cid == -1) return

        viewModelScope.launch {
            currentPage = 0
            hasMore = true
            isLoadingMore = false
            currentData = null

            _articleState.value = UiState.Loading
            val result = repository.getSystemArticles(
                page = currentPage,
                cid = cid,
                isRefresh = true
            )
            if (result is UiState.Success) {
                articleCollectRepository.seed(
                    result.data.articles
                )
                currentData = result.data
                hasMore = result.data.hasMore
                currentPage++
                _articleState.value = UiState.Success(result.data)
            } else {
                _articleState.value = result
            }
        }
    }

    fun loadMore() {
        if (cid == -1) return
        if (isLoadingMore || !hasMore) return

        viewModelScope.launch {
            isLoadingMore = true

            val result = repository.getSystemArticles(
                page = currentPage,
                cid = cid,
                isRefresh = false
            )

            if (result is UiState.Success) {
                articleCollectRepository.seed(
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
                _articleState.value = UiState.Success(mergedData)
            } else {
                _articleState.value = result
            }
            isLoadingMore = false
        }
    }

    fun toggleCollect(article: Article) {
        viewModelScope.launch {
            when (
                val result = articleCollectRepository.toggle(
                    articleId = article.id,
                    fallbackCollected = article.collect
                )
            ) {
                is UiState.Error -> {
                    _collectErrors.emit(result.message)
                }

                else -> Unit
            }
        }
    }
}
