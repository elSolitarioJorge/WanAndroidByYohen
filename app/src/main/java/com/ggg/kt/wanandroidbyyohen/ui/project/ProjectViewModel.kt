package com.ggg.kt.wanandroidbyyohen.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {
    private val repository = ProjectRepository()

    private val _tabState = MutableStateFlow<UiState<List<ProjectTab>>>(UiState.Loading)
    val tabState: StateFlow<UiState<List<ProjectTab>>> = _tabState

    fun loadProjectTabs() {
        viewModelScope.launch {
            _tabState.value = UiState.Loading

            when (val result = repository.getProjectTabs()) {
                is UiState.Success -> {
                    val tabs = mutableListOf<ProjectTab>()

                    tabs.add(
                        ProjectTab(
                            title = "最新项目",
                            isLatest = true
                        )
                    )

                    tabs.addAll(
                        result.data.map {
                            ProjectTab(
                                title = it.name,
                                cid = it.id,
                                isLatest = false
                            )
                        }
                    )

                    _tabState.value = UiState.Success(tabs)
                }

                is UiState.Error -> {
                    _tabState.value = UiState.Error(result.message)
                }

                is UiState.Loading -> {
                    _tabState.value = UiState.Loading
                }
            }
        }
    }
}