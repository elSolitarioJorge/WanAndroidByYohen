package com.ggg.kt.wanandroidbyyohen.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.ProjectListData
import com.ggg.kt.wanandroidbyyohen.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProjectListViewModel : ViewModel() {

    private val repository = ProjectRepository()

    private val _projectListState =
        MutableStateFlow<UiState<ProjectListData>>(UiState.Loading)
    val projectListState: StateFlow<UiState<ProjectListData>> = _projectListState

    private var tab: ProjectTab? = null

    private var currentPage = 0
    private var hasMore = true
    private var isLoadingMore = false

    fun setTab(projectTab: ProjectTab) {
        if (tab == null) {
            tab = projectTab
        }
    }

    fun refresh() {
        val currentTab = tab ?: return

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
                hasMore = result.data.hasMore
                currentPage++
            }
        }
    }

    fun loadMore() {
        val currentTab = tab ?: return
        if (isLoadingMore || !hasMore) return

        viewModelScope.launch {
            isLoadingMore = true

            val result = requestProjects(
                tab = currentTab,
                page = currentPage,
                isRefresh = false
            )

            _projectListState.value = result

            if (result is UiState.Success) {
                hasMore = result.data.hasMore
                currentPage++
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
}