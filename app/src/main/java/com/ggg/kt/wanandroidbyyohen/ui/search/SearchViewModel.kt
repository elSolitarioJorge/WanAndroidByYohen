package com.ggg.kt.wanandroidbyyohen.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.HotKey
import com.ggg.kt.wanandroidbyyohen.data.model.SearchArticleData
import com.ggg.kt.wanandroidbyyohen.data.repository.CollectRepository
import com.ggg.kt.wanandroidbyyohen.data.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val searchRepository = SearchRepository()
    private val collectRepository = CollectRepository()

    private val _hotKeyState =
        MutableStateFlow<UiState<List<HotKey>>>(UiState.Loading)
    val hotKeyState: StateFlow<UiState<List<HotKey>>> = _hotKeyState

    private val _searchState =
        MutableStateFlow<UiState<SearchArticleData>?>(null)
    val searchState: StateFlow<UiState<SearchArticleData>?> = _searchState

    private val _collectState =
        MutableStateFlow<UiState<Pair<Int, Boolean>>?>(null)
    val collectState: StateFlow<UiState<Pair<Int, Boolean>>?> = _collectState

    private var currentKeyword = ""
    private var currentPage = 0
    private var hasMore = true
    private var isLoadingMore = false
    private var currentData: SearchArticleData? = null

    fun loadHotKeys() {
        viewModelScope.launch {
            _hotKeyState.value = UiState.Loading
            _hotKeyState.value = searchRepository.getHotKeys()
        }
    }

    fun search(keyword: String) {
        val realKeyword = keyword.trim()
        if (realKeyword.isBlank()) {
            _searchState.value = UiState.Error("请输入搜索关键词")
            return
        }

        currentKeyword = realKeyword
        currentPage = 0
        hasMore = true
        isLoadingMore = false
        currentData = null

        viewModelScope.launch {
            _searchState.value = UiState.Loading

            val result = searchRepository.searchArticles(
                page = currentPage,
                keyword = currentKeyword,
                isRefresh = true
            )

            if (result is UiState.Success) {
                currentData = result.data
                hasMore = result.data.hasMore
                currentPage++
                _searchState.value = UiState.Success(result.data)
            } else {
                _searchState.value = result
            }
        }
    }

    fun refresh() {
        if (currentKeyword.isBlank()) return
        search(currentKeyword)
    }

    fun loadMore() {
        if (currentKeyword.isBlank()) return
        if (isLoadingMore || !hasMore) return

        viewModelScope.launch {
            isLoadingMore = true

            val result = searchRepository.searchArticles(
                page = currentPage,
                keyword = currentKeyword,
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
                _searchState.value = UiState.Success(mergedData)
            } else {
                _searchState.value = result
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
                    val collect = !article.collect
                    val updatedData = currentData?.copy(
                        articles = currentData?.articles.orEmpty().map {
                            if (it.id == article.id) it.copy(collect = collect) else it
                        }
                    )
                    currentData = updatedData
                    updatedData?.let { _searchState.value = UiState.Success(it) }
                    UiState.Success(article.id to collect)
                }
                is UiState.Error -> UiState.Error(result.message)
                is UiState.Loading -> UiState.Loading
            }
        }
    }
}
