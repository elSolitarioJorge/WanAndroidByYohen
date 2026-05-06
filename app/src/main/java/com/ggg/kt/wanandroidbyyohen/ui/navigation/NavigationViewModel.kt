package com.ggg.kt.wanandroidbyyohen.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.Chapter
import com.ggg.kt.wanandroidbyyohen.data.repository.NavigationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NavigationViewModel : ViewModel() {
    private val repository = NavigationRepository()

    private val _navigationState =
        MutableStateFlow<UiState<List<SectionUi<Article>>>>(UiState.Loading)
    val navigationState: StateFlow<UiState<List<SectionUi<Article>>>> = _navigationState

    private val _systemState =
        MutableStateFlow<UiState<List<SectionUi<Chapter>>>>(UiState.Loading)
    val systemState: StateFlow<UiState<List<SectionUi<Chapter>>>> = _systemState

    var currentMode = NavigationPageMode.NAVIGATION
        private set

    private var cachedNavigationSections: List<SectionUi<Article>>? = null
    private var cachedSystemSections: List<SectionUi<Chapter>>? = null

    private var hasRequestedNavigation = false
    private var hasRequestedSystem = false
    private var isLoadingNavigation = false
    private var isLoadingSystem = false

    private var navigationSelectedPosition = 0
    private var systemSelectedPosition = 0
    private var navigationScrollState = NavigationScrollState()
    private var systemScrollState = NavigationScrollState()

    fun setCurrentMode(mode: NavigationPageMode) {
        currentMode = mode
    }

    fun loadNavigationListIfNeeded() {
        if (cachedNavigationSections != null || hasRequestedNavigation || isLoadingNavigation) {
            return
        }

        refreshNavigationList()
    }

    fun loadSystemTreeIfNeeded() {
        if (cachedSystemSections != null || hasRequestedSystem || isLoadingSystem) {
            return
        }

        refreshSystemTree()
    }

    fun refreshNavigationList() {
        if (isLoadingNavigation) return

        hasRequestedNavigation = true
        isLoadingNavigation = true
        viewModelScope.launch {
            try {
                _navigationState.value = UiState.Loading

                _navigationState.value = when (val result = repository.getNavigationList()) {
                    is UiState.Success -> {
                        val sections = result.data.map {
                            SectionUi(
                                id = it.cid,
                                title = it.name,
                                items = it.articles
                            )
                        }
                        cachedNavigationSections = sections
                        navigationSelectedPosition = navigationSelectedPosition.coerceToSections(sections)
                        navigationScrollState = navigationScrollState.coerceToSections(sections)
                        UiState.Success(sections)
                    }

                    is UiState.Error -> UiState.Error(result.message)
                    is UiState.Loading -> UiState.Loading
                }
            } finally {
                isLoadingNavigation = false
            }
        }
    }

    fun refreshSystemTree() {
        if (isLoadingSystem) return

        hasRequestedSystem = true
        isLoadingSystem = true
        viewModelScope.launch {
            try {
                _systemState.value = UiState.Loading

                _systemState.value = when (val result = repository.getSystemTree()) {
                    is UiState.Success -> {
                        val sections = result.data.map {
                            SectionUi(
                                id = it.id,
                                title = it.name,
                                items = it.children.orEmpty()
                            )
                        }
                        cachedSystemSections = sections
                        systemSelectedPosition = systemSelectedPosition.coerceToSections(sections)
                        systemScrollState = systemScrollState.coerceToSections(sections)
                        UiState.Success(sections)
                    }

                    is UiState.Error -> UiState.Error(result.message)
                    is UiState.Loading -> UiState.Loading
                }
            } finally {
                isLoadingSystem = false
            }
        }
    }

    fun saveSelectedPosition(mode: NavigationPageMode, position: Int) {
        val safePosition = position.coerceToSections(getSections(mode))
        when (mode) {
            NavigationPageMode.NAVIGATION -> navigationSelectedPosition = safePosition
            NavigationPageMode.SYSTEM -> systemSelectedPosition = safePosition
        }
    }

    fun getSelectedPosition(mode: NavigationPageMode): Int {
        return when (mode) {
            NavigationPageMode.NAVIGATION -> navigationSelectedPosition
            NavigationPageMode.SYSTEM -> systemSelectedPosition
        }.coerceToSections(getSections(mode))
    }

    fun saveScrollState(
        mode: NavigationPageMode,
        position: Int,
        offset: Int
    ) {
        val scrollState = NavigationScrollState(
            position = position,
            offset = offset
        ).coerceToSections(getSections(mode))

        when (mode) {
            NavigationPageMode.NAVIGATION -> navigationScrollState = scrollState
            NavigationPageMode.SYSTEM -> systemScrollState = scrollState
        }
    }

    fun getScrollState(mode: NavigationPageMode): NavigationScrollState {
        return when (mode) {
            NavigationPageMode.NAVIGATION -> navigationScrollState
            NavigationPageMode.SYSTEM -> systemScrollState
        }.coerceToSections(getSections(mode))
    }

    fun getSectionCount(mode: NavigationPageMode): Int {
        return getSections(mode).size
    }

    fun getSectionTitle(
        mode: NavigationPageMode,
        position: Int
    ): String? {
        return getSections(mode).getOrNull(position)?.title
    }

    private fun getSections(mode: NavigationPageMode): List<SectionUi<*>> {
        return when (mode) {
            NavigationPageMode.NAVIGATION -> cachedNavigationSections
            NavigationPageMode.SYSTEM -> cachedSystemSections
        }.orEmpty()
    }

    private fun Int.coerceToSections(sections: List<SectionUi<*>>): Int {
        return if (sections.isEmpty()) {
            0
        } else {
            coerceIn(sections.indices)
        }
    }

    private fun NavigationScrollState.coerceToSections(
        sections: List<SectionUi<*>>
    ): NavigationScrollState {
        return if (sections.isEmpty()) {
            NavigationScrollState()
        } else {
            copy(position = position.coerceIn(sections.indices))
        }
    }
}