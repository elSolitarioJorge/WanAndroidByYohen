package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.data.model.Chapter
import com.ggg.kt.wanandroidbyyohen.data.model.ProjectListData

class ProjectRepository {

    suspend fun getProjectTabs(): UiState<List<Chapter>> {
        return try {
            val response = RetrofitClient.api.getProjectTree()

            if (response.errorCode == 0) {
                UiState.Success(response.data.orEmpty())
            } else {
                UiState.Error(response.errorMsg.ifBlank { "项目分类请求失败" })
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun getLatestProjects(
        page: Int,
        isRefresh: Boolean
    ): UiState<ProjectListData> {
        return try {
            val response = RetrofitClient.api.getLatestProjects(page)

            if (response.errorCode != 0) {
                return UiState.Error(response.errorMsg.ifBlank { "最新项目请求失败" })
            }

            val pageData = response.data

            UiState.Success(
                ProjectListData(
                    articles = pageData?.datas.orEmpty(),
                    isRefresh = isRefresh,
                    hasMore = pageData?.over != true
                )
            )
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }

    suspend fun getProjectList(
        page: Int,
        cid: Int,
        isRefresh: Boolean
    ): UiState<ProjectListData> {
        return try {
            val response = RetrofitClient.api.getProjectList(page, cid)

            if (response.errorCode != 0) {
                return UiState.Error(response.errorMsg.ifBlank { "项目列表请求失败" })
            }

            val pageData = response.data

            UiState.Success(
                ProjectListData(
                    articles = pageData?.datas.orEmpty(),
                    isRefresh = isRefresh,
                    hasMore = pageData?.over != true
                )
            )
        } catch (e: Exception) {
            UiState.Error(e.message ?: "网络异常")
        }
    }
}