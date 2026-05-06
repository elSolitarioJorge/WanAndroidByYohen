package com.ggg.kt.wanandroidbyyohen.ui.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.LOGIN_EXPIRED_MESSAGE
import com.ggg.kt.wanandroidbyyohen.data.model.MineUiState
import com.ggg.kt.wanandroidbyyohen.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MineViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _userInfoState =
        MutableStateFlow<MineUiState>(MineUiState.Loading)
    val userInfoState: StateFlow<MineUiState> = _userInfoState


    private val _logoutState =
        MutableStateFlow<UiState<Any>?>(null)
    val logoutState: StateFlow<UiState<Any>?> = _logoutState

    fun loadUserInfo() {
        viewModelScope.launch {
            if (!repository.isLoginLocal()) {
                _userInfoState.value = MineUiState.LoggedOut
                return@launch
            }

            val localUserInfo = repository.getLocalUserInfo()

            if (localUserInfo != null) {
                _userInfoState.value = MineUiState.Content(
                    userInfoData = localUserInfo,
                    isFromLocal = true
                )
            } else {
                _userInfoState.value = MineUiState.Loading
            }

            when (val remoteResult = repository.getUserInfo()) {
                is UiState.Success -> {
                    _userInfoState.value = MineUiState.Content(
                        userInfoData = remoteResult.data,
                        isFromLocal = false
                    )
                }

                is UiState.Error -> {
                    if (remoteResult.message == LOGIN_EXPIRED_MESSAGE) {
                        _userInfoState.value = MineUiState.LoggedOut
                    } else if (localUserInfo == null) {
                        _userInfoState.value = MineUiState.Error(remoteResult.message)
                    } else {
                        _userInfoState.value = MineUiState.Content(
                            userInfoData = localUserInfo,
                            isFromLocal = true
                        )
                    }
                }

                is UiState.Loading -> Unit
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = UiState.Loading
            _logoutState.value = repository.logout()
        }
    }
}
