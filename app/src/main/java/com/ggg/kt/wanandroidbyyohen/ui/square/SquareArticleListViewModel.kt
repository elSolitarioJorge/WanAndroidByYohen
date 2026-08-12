package com.ggg.kt.wanandroidbyyohen.ui.square

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.collect.ArticleCollectProvider
import com.ggg.kt.wanandroidbyyohen.data.collect.ArticleCollectState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.SquareData
import com.ggg.kt.wanandroidbyyohen.data.model.SquareTag
import com.ggg.kt.wanandroidbyyohen.data.repository.SearchRepository
import com.ggg.kt.wanandroidbyyohen.data.repository.SquareRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SquareArticleListViewModel : ViewModel() {
    private val squareRepository = SquareRepository()
    private val searchRepository = SearchRepository()

    private val _squareState = MutableStateFlow<UiState<SquareData>>(UiState.Loading)
    val squareState: StateFlow<UiState<SquareData>> = _squareState
    private val articleCollectRepository = ArticleCollectProvider.repository

    val collectStates: StateFlow<Map<Int, ArticleCollectState>> = articleCollectRepository.states

    private val _collectErrors = MutableSharedFlow<String>(extraBufferCapacity = 1)

    val collectErrors: SharedFlow<String> = _collectErrors.asSharedFlow()

    private var tag: SquareTag? = null
    private var currentData: SquareData? = null
    private var currentPage = 0
    private var hasMore = true
    private var isLoadingMore = false
    private var hasLoaded = false

    fun setTag(newTag: SquareTag) {
        if (tag?.key == newTag.key) return

        tag = newTag
        currentData = null
        currentPage = 0
        hasMore = true
        isLoadingMore = false
        hasLoaded = false
    }

    fun loadIfNeeded() {
        if (hasLoaded) return

        refresh()
    }

    fun refresh() {
        val currentTag = tag ?: return
        hasLoaded = true
        viewModelScope.launch {
            currentPage = 0
            hasMore = true
            isLoadingMore = false

            _squareState.value = UiState.Loading
            val result = loadArticles(
                tag = currentTag,
                page = 0,
                isRefresh = true
            )
            if (result is UiState.Success) {
                articleCollectRepository.seed(result.data.articles)
                currentData = result.data
                currentPage = 1
                hasMore = result.data.hasMore
            }
            _squareState.value = result
        }
    }

    fun loadMore() {
        val currentTag = tag ?: return
        val oldData = currentData ?: return
        if (isLoadingMore || !hasMore) return

        viewModelScope.launch {
            isLoadingMore = true
            val result = loadArticles(
                tag = currentTag,
                page = currentPage,
                isRefresh = false
            )

            if (result is UiState.Success) {
                articleCollectRepository.seed(result.data.articles)
                val mergedData = oldData.copy(
                    articles = oldData.articles + result.data.articles,
                    isRefresh = false,
                    hasMore = result.data.hasMore
                )
                currentData = mergedData
                currentPage++
                hasMore = result.data.hasMore
                _squareState.value = UiState.Success(mergedData)
            } else {
                _squareState.value = result
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

    private suspend fun loadArticles(
        tag: SquareTag,
        page: Int,
        isRefresh: Boolean
    ): UiState<SquareData> {
        val keyword = tag.keyword
        return if (keyword == null) {
            squareRepository.getSquareArticles(page, isRefresh)
        } else {
            when (
                val result = searchRepository.searchArticles(page, keyword, isRefresh)
            ) {
                is UiState.Success -> UiState.Success(
                    SquareData(
                        articles = result.data.articles,
                        isRefresh = result.data.isRefresh,
                        hasMore = result.data.hasMore
                    )
                )

                is UiState.Error -> UiState.Error(result.message)

                is UiState.Loading -> UiState.Loading
            }
        }
    }
}
