# WanAndroid Kotlin Client

基于 WanAndroid 开放 API 开发的 Android 客户端，采用 Kotlin + MVVM 架构，主要用于实践 Android Jetpack、网络请求、登录态、收藏体系、分页列表和复杂页面结构设计。

## 技术栈

- Kotlin
- XML + ViewBinding
- MVVM
- ViewModel + StateFlow
- Retrofit + OkHttp
- RecyclerView
- Navigation Component
- BottomNavigationView
- SwipeRefreshLayout
- Coil
- FlexboxLayoutManager
- WebView
- SharedPreferences

## 功能模块

- 首页：Banner、置顶文章、文章列表、刷新、加载更多、收藏
- 广场：用户分享文章、分享文章、刷新、加载更多
- 项目：项目分类、最新项目、项目卡片、图片加载
- 导航：导航/体系双 Tab、左右联动、标签流
- 我的：登录、注册、用户信息、我的收藏、我的分享、退出登录
- 搜索：热搜词、关键词搜索、搜索结果分页
- WebView：文章详情、内部链接处理、返回栈处理

## 架构设计

项目采用 Single Activity + 多 Fragment 架构。
