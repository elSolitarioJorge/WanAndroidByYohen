package com.ggg.kt.wanandroidbyyohen.data.collect

object ArticleCollectProvider {
    val repository: ArticleCollectRepository by lazy {
        ArticleCollectRepository(
            store = ArticleCollectStore(),
            remoteDataSource = NetworkCollectRemoteDataSource()
        )
    }
}