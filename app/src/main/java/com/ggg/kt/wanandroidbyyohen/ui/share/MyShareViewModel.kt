package com.ggg.kt.wanandroidbyyohen.ui.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.MyShareArticleData
import com.ggg.kt.wanandroidbyyohen.data.repository.ShareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyShareViewModel : ViewModel() {

    private val repository = ShareRepository()

    private val _myShareState =
        MutableStateFlow<UiState<MyShareArticleData>>(UiState.Loading)
    val myShareState: StateFlow<UiState<MyShareArticleData>> = _myShareState

    private val _deleteState =
        MutableStateFlow<UiState<Article>?>(null)
    val deleteState: StateFlow<UiState<Article>?> = _deleteState

    private var currentPage = 1
    private var hasMore = true
    private var isLoadingMore = false

    fun refresh() {
        viewModelScope.launch {
            currentPage = 1
            hasMore = true
            isLoadingMore = false

            _myShareState.value = UiState.Loading

            val result = repository.getMyShareArticles(
                page = currentPage,
                isRefresh = true
            )

            _myShareState.value = result

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

            val result = repository.getMyShareArticles(
                page = currentPage,
                isRefresh = false
            )

            _myShareState.value = result

            if (result is UiState.Success) {
                hasMore = result.data.hasMore
                currentPage++
            }

            isLoadingMore = false
        }
    }

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading

            val result = repository.deleteMyShareArticle(article.id)

            _deleteState.value = when (result) {
                is UiState.Success -> UiState.Success(article)
                is UiState.Error -> UiState.Error(result.message)
                is UiState.Loading -> UiState.Loading
            }
        }
    }
}