package com.ggg.kt.wanandroidbyyohen.ui.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.UserInfoData
import com.ggg.kt.wanandroidbyyohen.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MineViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _userInfoState =
        MutableStateFlow<UiState<UserInfoData>?>(null)
    val userInfoState: StateFlow<UiState<UserInfoData>?> = _userInfoState

    private val _logoutState =
        MutableStateFlow<UiState<Any>?>(null)
    val logoutState: StateFlow<UiState<Any>?> = _logoutState

    fun loadUserInfo() {
        viewModelScope.launch {
            _userInfoState.value = UiState.Loading
            _userInfoState.value = repository.getUserInfo()
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = UiState.Loading
            _logoutState.value = repository.logout()
        }
    }
}