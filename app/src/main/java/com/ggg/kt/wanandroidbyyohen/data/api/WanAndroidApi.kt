package com.ggg.kt.wanandroidbyyohen.data.api

import com.ggg.kt.wanandroidbyyohen.data.model.ApiResponse
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.Banner
import com.ggg.kt.wanandroidbyyohen.data.model.Chapter
import com.ggg.kt.wanandroidbyyohen.data.model.HotKey
import com.ggg.kt.wanandroidbyyohen.data.model.MyShareResponse
import com.ggg.kt.wanandroidbyyohen.data.model.Navigation
import com.ggg.kt.wanandroidbyyohen.data.model.PageResponse
import com.ggg.kt.wanandroidbyyohen.data.model.User
import com.ggg.kt.wanandroidbyyohen.data.model.UserInfoData
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
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

    @GET("navi/json")
    suspend fun getNavigationList(): ApiResponse<List<Navigation>>

    @GET("tree/json")
    suspend fun getSystemTree(): ApiResponse<List<Chapter>>

    @GET("article/list/{page}/json")
    suspend fun getArticleByCid(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ) : ApiResponse<PageResponse<Article>>

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResponse<User>

    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): ApiResponse<User>

    @GET("user/logout/json")
    suspend fun logout(): ApiResponse<Any>

    @GET("user/lg/userinfo/json")
    suspend fun getUserInfo(): ApiResponse<UserInfoData>

    @POST("lg/collect/{id}/json")
    suspend fun collectArticle(
        @Path("id") id: Int
    ): ApiResponse<Any>

    @POST("lg/uncollect_originId/{id}/json")
    suspend fun uncollectArticle(
        @Path("id") id: Int
    ): ApiResponse<Any>

    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectArticles(
        @Path("page") page: Int
    ): ApiResponse<PageResponse<Article>>

    @FormUrlEncoded
    @POST("lg/uncollect/{id}/json")
    suspend fun uncollectArticleFromMine(
        @Path("id") id: Int,
        @Field("originId") originId: Int
    ): ApiResponse<Any>

    @GET("hotkey/json")
    suspend fun getHotKeys(): ApiResponse<List<HotKey>>

    @FormUrlEncoded
    @POST("article/query/{page}/json")
    suspend fun searchArticles(
        @Path("page") page: Int,
        @Field("k") keyword: String
    ): ApiResponse<PageResponse<Article>>

    @FormUrlEncoded
    @POST("lg/user_article/add/json")
    suspend fun shareArticle(
        @Field("title") title: String,
        @Field("link") link: String
    ): ApiResponse<Any>

    @GET("user/lg/private_articles/{page}/json")
    suspend fun getMyShareArticles(
        @Path("page") page: Int
    ): ApiResponse<MyShareResponse>

    @POST("lg/user_article/delete/{id}/json")
    suspend fun deleteMyShareArticle(
        @Path("id") id: Int
    ): ApiResponse<Any>
}