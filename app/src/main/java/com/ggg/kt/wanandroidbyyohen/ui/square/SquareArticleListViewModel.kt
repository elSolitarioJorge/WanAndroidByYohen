package com.ggg.kt.wanandroidbyyohen.ui.square

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.SquareData
import com.ggg.kt.wanandroidbyyohen.data.model.SquareTag
import com.ggg.kt.wanandroidbyyohen.data.repository.CollectRepository
import com.ggg.kt.wanandroidbyyohen.data.repository.SearchRepository
import com.ggg.kt.wanandroidbyyohen.data.repository.SquareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SquareArticleListViewModel : ViewModel() {
    private val squareRepository = SquareRepository()
    private val collectRepository = CollectRepository()
    private val searchRepository = SearchRepository()

    private val _squareState = MutableStateFlow<UiState<SquareData>>(UiState.Loading)
    val squareState: StateFlow<UiState<SquareData>> = _squareState

    private val _collectState = MutableStateFlow<UiState<Pair<Int, Boolean>>?>(null)
    val collectState: StateFlow<UiState<Pair<Int, Boolean>>?> = _collectState

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
            _collectState.value = UiState.Loading

            val result = if (article.collect) {
                collectRepository.uncollectArticle(article.id)
            } else {
                collectRepository.collectArticle(article.id)
            }

            _collectState.value = when (result) {
                is UiState.Success -> {
                    updateCurrentCollectState(article.id, !article.collect)
                    UiState.Success(article.id to !article.collect)
                }

                is UiState.Error -> UiState.Error(result.message)

                is UiState.Loading -> UiState.Loading
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

    private fun updateCurrentCollectState(articleId: Int, collect: Boolean) {
        val data = currentData ?: return
        currentData = data.copy(
            articles = data.articles.map { article ->
                if (article.id == articleId) {
                    article.copy(collect = collect)
                } else {
                    article
                }
            }
        )
    }
}
