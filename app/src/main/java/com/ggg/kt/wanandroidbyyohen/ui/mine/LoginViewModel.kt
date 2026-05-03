package com.ggg.kt.wanandroidbyyohen.ui.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.User
import com.ggg.kt.wanandroidbyyohen.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _loginState = MutableStateFlow<UiState<User>?>(null)
    val loginState: StateFlow<UiState<User>?> = _loginState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("用户名和密码不能为空")
            return
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            _loginState.value = repository.login(username, password)
        }
    }

    fun register(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("用户名和密码不能为空")
            return
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            _loginState.value = repository.register(
                username = username,
                password = password,
                repassword = password
            )
        }
    }
}