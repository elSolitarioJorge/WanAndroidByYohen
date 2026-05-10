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
    private var currentData: MyShareArticleData? = null

    fun refresh() {
        viewModelScope.launch {
            currentPage = 1
            hasMore = true
            isLoadingMore = false
            currentData = null

            _myShareState.value = UiState.Loading

            val result = repository.getMyShareArticles(
                page = currentPage,
                isRefresh = true
            )

            if (result is UiState.Success) {
                currentData = result.data
                hasMore = result.data.hasMore
                currentPage++
                _myShareState.value = UiState.Success(result.data)
            } else {
                _myShareState.value = result
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

            if (result is UiState.Success) {
                val oldData = currentData
                val mergedData = oldData?.copy(
                    articles = oldData.articles + result.data.articles,
                    isRefresh = false,
                    hasMore = result.data.hasMore
                ) ?: result.data
                currentData = mergedData
                hasMore = result.data.hasMore
                currentPage++
                _myShareState.value = UiState.Success(mergedData)
            } else {
                _myShareState.value = result
            }

            isLoadingMore = false
        }
    }

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading

            val result = repository.deleteMyShareArticle(article.id)

            _deleteState.value = when (result) {
                is UiState.Success -> {
                    val updatedData = currentData?.copy(
                        articles = currentData?.articles.orEmpty().filter { it.id != article.id }
                    )
                    currentData = updatedData
                    updatedData?.let { _myShareState.value = UiState.Success(it) }
                    UiState.Success(article)
                }
                is UiState.Error -> UiState.Error(result.message)
                is UiState.Loading -> UiState.Loading
            }
        }
    }
}
