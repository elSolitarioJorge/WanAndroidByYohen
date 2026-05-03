package com.ggg.kt.wanandroidbyyohen.ui.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.MineUiData
import com.ggg.kt.wanandroidbyyohen.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MineViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _userInfoState =
        MutableStateFlow<UiState<MineUiData>?>(null)
    val userInfoState: StateFlow<UiState<MineUiData>?> = _userInfoState


    private val _logoutState =
        MutableStateFlow<UiState<Any>?>(null)
    val logoutState: StateFlow<UiState<Any>?> = _logoutState

    fun loadUserInfo() {
        viewModelScope.launch {
            val localUserInfo = repository.getLocalUserInfo()

            if (localUserInfo != null) {
                _userInfoState.value = UiState.Success(
                    MineUiData(
                        userInfoData = localUserInfo,
                        isFromLocal = true
                    )
                )
            } else {
                _userInfoState.value = UiState.Loading
            }

            when (val remoteResult = repository.getUserInfo()) {
                is UiState.Success -> {
                    _userInfoState.value = UiState.Success(
                        MineUiData(
                            userInfoData = remoteResult.data,
                            isFromLocal = false
                        )
                    )
                }

                is UiState.Error -> {
                    if (localUserInfo == null) {
                        _userInfoState.value = UiState.Error(remoteResult.message)
                    } else {
                        // 有本地登录信息，网络失败时继续显示本地数据
                        _userInfoState.value = UiState.Success(
                            MineUiData(
                                userInfoData = localUserInfo,
                                isFromLocal = true
                            )
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