package com.ggg.kt.wanandroidbyyohen.ui.square

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.SquareData
import com.ggg.kt.wanandroidbyyohen.data.repository.CollectRepository
import com.ggg.kt.wanandroidbyyohen.data.repository.SquareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SquareViewModel : ViewModel() {
    private val repository = SquareRepository()
    private val collectRepository = CollectRepository()

    private val _collectState = MutableStateFlow<UiState<Pair<Int, Boolean>>?>(null)
    val collectState: StateFlow<UiState<Pair<Int, Boolean>>?> = _collectState
    private val _squareState = MutableStateFlow<UiState<SquareData>>(UiState.Loading)
    val squareState: StateFlow<UiState<SquareData>> = _squareState

    private var currentPage = 0
    private var hasMore = true
    private var isLoadingMore = false

    fun refreshSquareArticles() {
        viewModelScope.launch {
            currentPage = 0
            hasMore = true
            isLoadingMore = false

            _squareState.value = UiState.Loading
            val result = repository.getSquareArticles(
                page = 0,
                isRefresh = true
            )
            _squareState.value = result
            if (result is UiState.Success) {
                hasMore = result.data.hasMore
                currentPage = 1
            }
        }
    }

    fun loadMoreSquareArticles() {
        if (isLoadingMore || !hasMore) return

        viewModelScope.launch {
            isLoadingMore = true
            val result = repository.getSquareArticles(
                page = currentPage,
                isRefresh = false
            )
            _squareState.value = result

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