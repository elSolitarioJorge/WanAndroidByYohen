package com.ggg.kt.wanandroidbyyohen.data.api

import com.ggg.kt.wanandroidbyyohen.data.model.ApiResponse
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.Banner
import com.ggg.kt.wanandroidbyyohen.data.model.Chapter
import com.ggg.kt.wanandroidbyyohen.data.model.PageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WanAndroidApi {
    @GET("article/list/{page}/json")
    suspend fun getHomeArticles(
        @Path("page") page: Int
    ): ApiResponse<PageResponse<Article>>

    @GET("banner/json")
    suspend fun getBanners(): ApiResponse<List<Banner>>

    @GET("article/top/json")
    suspend fun getTopArticles(): ApiResponse<List<Article>>

    @GET("user_article/list/{page}/json")
    suspend fun getSquareArticles(
        @Path("page") page: Int
    ): ApiResponse<PageResponse<Article>>

    @GET("article/listproject/{page}/json")
    suspend fun getLatestProjects(
        @Path("page") page: Int
    ): ApiResponse<PageResponse<Article>>

    @GET("project/tree/json")
    suspend fun getProjectTree(): ApiResponse<List<Chapter>>

    @GET("project/list/{page}/json")
    suspend fun getProjectList(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ApiResponse<PageResponse<Article>>
}