package com.ggg.kt.wanandroidbyyohen.ui.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.CollectArticleData
import com.ggg.kt.wanandroidbyyohen.data.repository.CollectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CollectArticleViewModel : ViewModel() {
    private val repository = CollectRepository()
    private val _collectArticleState =
        MutableStateFlow<UiState<CollectArticleData>>(UiState.Loading)
    val collectArticleState: StateFlow<UiState<CollectArticleData>> = _collectArticleState

    private val _uncollectState =
        MutableStateFlow<UiState<Article>?>(null)
    val uncollectState: StateFlow<UiState<Article>?> = _uncollectState

    private var currentPage = 0
    private var hasMore = true
    private var isLoadingMore = false

    fun refresh() {
        viewModelScope.launch {
            currentPage = 0
            hasMore = true
            isLoadingMore = false

            _collectArticleState.value = UiState.Loading

            val result = repository.getCollectArticles(
                page = currentPage,
                isRefresh = true
            )

            _collectArticleState.value = result

            if (result is UiState.Success) {
                hasMore = result.data.hasMore
                currentPage++
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

            _collectArticleState.value = result

            if (result is UiState.Success) {
                hasMore = result.data.hasMore
                currentPage++
            }

            isLoadingMore = false
        }
    }

    fun uncollect(article: Article) {
        viewModelScope.launch {
            _uncollectState.value = UiState.Loading

            val result = repository.uncollectArticleFromMine(
                id = article.id,
                originId = article.originId
            )

            _uncollectState.value = when (result) {
                is UiState.Success -> UiState.Success(article)
                is UiState.Error -> UiState.Error(result.message)
                is UiState.Loading -> UiState.Loading
            }
        }
    }
}