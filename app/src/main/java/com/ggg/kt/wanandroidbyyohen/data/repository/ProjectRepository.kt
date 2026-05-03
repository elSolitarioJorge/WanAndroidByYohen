package com.ggg.kt.wanandroidbyyohen.data.repository

import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.network.RetrofitClient
import com.ggg.kt.wanandroidbyyohen.common.network.safeApiCall
import com.ggg.kt.wanandroidbyyohen.data.model.Chapter
import com.ggg.kt.wanandroidbyyohen.data.model.ProjectListData

class ProjectRepository {

    suspend fun getProjectTabs(): UiState<List<Chapter>> {
        return safeApiCall(
            defaultErrorMessage = "项目分类请求失败"
        ) {
            RetrofitClient.api.getProjectTree()
        }
    }

    suspend fun getLatestProjects(
        page: Int,
        isRefresh: Boolean
    ): UiState<ProjectListData> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "最新项目请求失败"
            ) {
                RetrofitClient.api.getLatestProjects(page)
            }
        ) {
            is UiState.Success -> {
                val pageData = result.data

                UiState.Success(
                    ProjectListData(
                        articles = pageData.datas,
                        isRefresh = isRefresh,
                        hasMore = !pageData.over
                    )
                )
            }

            is UiState.Error -> UiState.Error(result.message)

            is UiState.Loading -> UiState.Loading
        }
    }

    suspend fun getProjectList(
        page: Int,
        cid: Int,
        isRefresh: Boolean
    ): UiState<ProjectListData> {
        return when (
            val result = safeApiCall(
                defaultErrorMessage = "项目列表请求失败"
            ) {
                RetrofitClient.api.getProjectList(page, cid)
            }
        ) {
            is UiState.Success -> {
                val pageData = result.data

                UiState.Success(
                    ProjectListData(
                        articles = pageData.datas,
                        isRefresh = isRefresh,
                        hasMore = !pageData.over
                    )
                )
            }

            is UiState.Error -> UiState.Error(result.message)

            is UiState.Loading -> UiState.Loading
        }
    }
}
