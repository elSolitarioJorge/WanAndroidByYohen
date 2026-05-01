package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.data.model.Navigation

class NavigationRepository {
    suspend fun getNavigationList(): UiState<List<Navigation>> {
        return try {
            val response = RetrofitClient.api.getNavigationList()

            if (response.errorCode == 0) {
                UiState.Success(response.data.orEmpty())
            } else {
                UiState.Error(response.errorMsg.ifBlank { "导航数据请求失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }
}