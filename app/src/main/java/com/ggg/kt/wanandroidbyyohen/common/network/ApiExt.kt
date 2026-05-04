package com.ggg.kt.wanandroidbyyohen.common.network

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.local.UserStore
import com.ggg.kt.wanandroidbyyohen.data.model.ApiResponse

const val LOGIN_EXPIRED_MESSAGE = "您还未登录，请登录后再进行该操作"

suspend fun <T> safeApiCall(
    defaultErrorMessage: String = "请求失败",
    block: suspend () -> ApiResponse<T>
): UiState<T> {
    return try {
        val response = block()
        when (response.errorCode) {
            0 -> {
                val data = response.data
                if (data != null) {
                    UiState.Success(data)
                } else {
                    UiState.Error("数据为空")
                }
            }

            -1001 -> {
                RetrofitClient.cookieJar.clear()
                UserStore.clear()
                UiState.Error(LOGIN_EXPIRED_MESSAGE)
            }

            else -> {
                UiState.Error(
                    response.errorMsg.ifBlank { defaultErrorMessage }
                )
            }
        }
    } catch (e: Exception) {
        UiState.Error(e.message ?: "网络异常")
    }
}

suspend fun safeApiCallWithoutData(
    defaultErrorMessage: String = "请求失败",
    block: suspend () -> ApiResponse<Any>
): UiState<Any> {
    return try {
        val response = block()

        when (response.errorCode) {
            0 -> {
                UiState.Success(Any())
            }

            -1001 -> {
                RetrofitClient.cookieJar.clear()
                UserStore.clear()
                UiState.Error(LOGIN_EXPIRED_MESSAGE)
            }

            else -> {
                UiState.Error(
                    response.errorMsg.ifBlank { defaultErrorMessage }
                )
            }
        }
    } catch (e: Exception) {
        UiState.Error(e.message ?: "网络异常")
    }
}