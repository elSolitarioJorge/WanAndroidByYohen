package com.ggg.kt.wanandroidbyyohen.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.collect.ArticleCollectProvider
import com.ggg.kt.wanandroidbyyohen.data.collect.ArticleCollectState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.ProjectListData
import com.ggg.kt.wanandroidbyyohen.data.model.ProjectTab
import com.ggg.kt.wanandroidbyyohen.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ProjectListViewModel : ViewModel() {

    private val repository = ProjectRepository()
    private val articleCollectRepository = ArticleCollectProvider.repository

    val collectStates: StateFlow<Map<Int, ArticleCollectState>> = articleCollectRepository.states

    private val _collectErrors = MutableSharedFlow<String>(extraBufferCapacity = 1)

    val collectErrors: SharedFlow<String> = _collectErrors.asSharedFlow()

    private val _projectListState =
        MutableStateFlow<UiState<ProjectListData>>(UiState.Loading)
    val projectListState: StateFlow<UiState<ProjectListData>> = _projectListState

    private var tab: ProjectTab? = null
    private var currentData: ProjectListData? = null

    private var currentPage = 0
    private var hasMore = true
    private var isLoadingMore = false
    private var isRefreshing = false
    private var hasLoaded = false

    fun setTab(projectTab: ProjectTab) {
        if (tab == projectTab) return

        tab = projectTab
        currentData = null
        currentPage = 0
        hasMore = true
        isLoadingMore = false
        isRefreshing = false
        hasLoaded = false
        _projectListState.value = UiState.Loading
    }

    fun loadIfNeeded() {
        if (hasLoaded || isRefreshing) return

        refresh()
    }

    fun refresh() {
        val currentTab = tab ?: return
        if (isRefreshing) return

        isRefreshing = true
        viewModelScope.launch {
            currentPage = if (currentTab.isLatest) 0 else 1
            hasMore = true
            isLoadingMore = false

            _projectListState.value = UiState.Loading

            val result = requestProjects(
                tab = currentTab,
                page = currentPage,
                isRefresh = true
            )

            _projectListState.value = result

            if (result is UiState.Success) {
                articleCollectRepository.seed(
                    result.data.articles
                )
                currentData = result.data
                hasLoaded = true
                hasMore = result.data.hasMore
                currentPage++
            } else if (result is UiState.Error) {
                hasLoaded = false
            }

            isRefreshing = false
        }
    }

    fun loadMore() {
        val currentTab = tab ?: return
        val oldData = currentData ?: return
        if (isLoadingMore || isRefreshing || !hasMore) return

        isLoadingMore = true
        viewModelScope.launch {
            val result = requestProjects(
                tab = currentTab,
                page = currentPage,
                isRefresh = false
            )

            if (result is UiState.Success) {
                articleCollectRepository.seed(
                    result.data.articles
                )
                val mergedData = oldData.copy(
                    articles = oldData.articles + result.data.articles,
                    isRefresh = false,
                    hasMore = result.data.hasMore
                )
                currentData = mergedData
                hasMore = result.data.hasMore
                currentPage++
                _projectListState.value = UiState.Success(mergedData)
            } else {
                _projectListState.value = result
            }

            isLoadingMore = false
        }
    }

    private suspend fun requestProjects(
        tab: ProjectTab,
        page: Int,
        isRefresh: Boolean
    ): UiState<ProjectListData> {
        return if (tab.isLatest) {
            repository.getLatestProjects(
                page = page,
                isRefresh = isRefresh
            )
        } else {
            val cid = tab.cid
            if (cid == null) {
                UiState.Error("项目分类 id 为空")
            } else {
                repository.getProjectList(
                    page = page,
                    cid = cid,
                    isRefresh = isRefresh
                )
            }
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
