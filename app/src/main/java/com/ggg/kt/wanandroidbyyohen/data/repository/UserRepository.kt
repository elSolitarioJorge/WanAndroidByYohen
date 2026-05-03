package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCall
import com.ggg.kt.wanandroidbyyohen.data.local.UserStore
import com.ggg.kt.wanandroidbyyohen.data.model.User
import com.ggg.kt.wanandroidbyyohen.data.model.UserInfoData

class UserRepository {
    suspend fun login(
        username: String,
        password: String
    ): UiState<User> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "登录失败"
            ) {
                RetrofitClient.api.login(username, password)
            }
        ) {
            is UiState.Success -> {
                val user = result.data
                UserStore.saveLoginUser(user)
                UiState.Success(user)
            }

            is UiState.Error -> UiState.Error(result.message)

            is UiState.Loading -> UiState.Loading
        }
    }

    suspend fun register(
        username: String,
        password: String,
        repassword: String
    ): UiState<User> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "注册失败"
            ) {
                RetrofitClient.api.register(username, password, repassword)
            }
        ) {
            is UiState.Success -> UiState.Success(result.data)

            is UiState.Error -> UiState.Error(result.message)

            is UiState.Loading -> UiState.Loading
        }
    }

    suspend fun getUserInfo(): UiState<UserInfoData> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "获取用户信息失败"
            ) {
                RetrofitClient.api.getUserInfo()
            }
        ) {
            is UiState.Success -> {
                UserStore.saveUserInfo(result.data)
                UiState.Success(result.data)
            }

            is UiState.Error -> UiState.Error(result.message)

            is UiState.Loading -> UiState.Loading
        }
    }

    fun logout(): UiState<Any> {
        return try {
            RetrofitClient.cookieJar.clear()
            UserStore.clear()
            UiState.Success(Any())
        } catch (e: Exception) {
            UiState.Error(e.message ?: "退出登录失败")
        }
    }

    fun getLocalUserInfo(): UserInfoData? {
        return UserStore.getLocalUserInfo()
    }

    fun isLoginLocal(): Boolean {
        return UserStore.isLogin()
    }
}