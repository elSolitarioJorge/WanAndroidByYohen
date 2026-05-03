package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.data.model.User
import com.ggg.kt.wanandroidbyyohen.data.model.UserInfoData

class UserRepository {
    suspend fun login(
        username: String,
        password: String
    ): UiState<User> {
        return try {
            val response = RetrofitClient.api.login(username, password)
            if (response.errorCode == 0) {
                UiState.Success(response.data ?: User(username = username))
            } else {
                UiState.Error(response.errorMsg.ifBlank { "登录失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun register(
        username: String,
        password: String,
        repassword: String
    ): UiState<User> {
        return try {
            val response = RetrofitClient.api.register(username, password, repassword)

            if (response.errorCode == 0) {
                UiState.Success(response.data ?: User(username = username))
            } else {
                UiState.Error(response.errorMsg.ifBlank { "注册失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络请求")
        }
    }

    suspend fun getUserInfo(): UiState<UserInfoData> {
        return try {
            val response = RetrofitClient.api.getUserInfo()
            if (response.errorCode == 0) {
                val data = response.data
                if (data != null) {
                    UiState.Success(data)
                } else {
                    UiState.Error("用户信息为空")
                }
            } else {
                UiState.Error(response.errorMsg.ifBlank { "获取用户信息失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun logout(): UiState<Any> {
        return try {
            val response = RetrofitClient.api.logout()
            if (response.errorCode == 0) {
                RetrofitClient.cookieJar.clear()
                UiState.Success(Any())
            } else {
                UiState.Error(response.errorMsg.ifBlank { "退出登录失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }
}