package com.ggg.kt.wanandroidbyyohen.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Navigation
import com.ggg.kt.wanandroidbyyohen.data.repository.NavigationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NavigationViewModel : ViewModel() {
    private val repository = NavigationRepository()

    private val _navigationState =
        MutableStateFlow<UiState<List<Navigation>>>(UiState.Loading)
    val navigationState: StateFlow<UiState<List<Navigation>>> = _navigationState

    fun loadNavigationList() {
        viewModelScope.launch {
            _navigationState.value = UiState.Loading
            _navigationState.value = repository.getNavigationList()
        }
    }
}