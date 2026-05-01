package com.ggg.kt.wanandroidbyyohen.data.api

import com.ggg.kt.wanandroidbyyohen.data.model.ApiResponse
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.Banner
import com.ggg.kt.wanandroidbyyohen.data.model.PageResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface WanAndroidApi {
    @GET("article/list/{page}/json")
    suspend fun getHomeArticles(
        @Path("page") page: Int
    ): ApiResponse<PageResponse<Article>>

    @GET("banner/json")
    suspend fun getBanners(): ApiResponse<List<Banner>>

    @GET("article/top/json")
    suspend fun getTopArticles(): ApiResponse<List<Article>>
}