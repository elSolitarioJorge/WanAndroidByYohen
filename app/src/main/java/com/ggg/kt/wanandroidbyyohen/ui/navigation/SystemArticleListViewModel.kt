package com.ggg.kt.wanandroidbyyohen.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.SystemArticleData
import com.ggg.kt.wanandroidbyyohen.data.repository.CollectRepository
import com.ggg.kt.wanandroidbyyohen.data.repository.NavigationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SystemArticleListViewModel : ViewModel() {
    private val repository = NavigationRepository()

    private val collectRepository = CollectRepository()

    private val _collectState = MutableStateFlow<UiState<Pair<Int, Boolean>>?>(null)
    val collectState: StateFlow<UiState<Pair<Int, Boolean>>?> = _collectState
    private val _articleState =
        MutableStateFlow<UiState<SystemArticleData>>(UiState.Loading)
    val articleState: StateFlow<UiState<SystemArticleData>> = _articleState
    private var cid: Int = -1
    private var currentPage = 0
    private var hasMore = true
    private var isLoadingMore = false

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

            _articleState.value = UiState.Loading
            val result = repository.getSystemArticles(
                page = currentPage,
                cid = cid,
                isRefresh = true
            )
            _articleState.value = result
            if (result is UiState.Success) {
                hasMore = result.data.hasMore
                currentPage++
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

            _articleState.value = result
            if (result is UiState.Success) {
                hasMore = result.data.hasMore
                currentPage++
            }
            isLoadingMore = false
        }
    }

    fun toggleCollect(article: Article) {
        viewModelScope.launch {
            _collectState.value = UiState.Loading

            val result = if (article.collect) {
                collectRepository.uncollectArticle(article.id)
            } else {
                collectRepository.collectArticle(article.id)
            }

            _collectState.value = when (result) {
                is UiState.Success -> UiState.Success(article.id to !article.collect)
                is UiState.Error -> UiState.Error(result.message)
                is UiState.Loading -> UiState.Loading
            }
        }
    }
}