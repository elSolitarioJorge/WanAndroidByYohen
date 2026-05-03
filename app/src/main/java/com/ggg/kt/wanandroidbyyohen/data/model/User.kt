package com.ggg.kt.wanandroidbyyohen.data.model

data class User(
    val id: Int = 0,
    val username: String = "",
    val nickname: String? = null,
    val icon: String? = null,
    val email: String? = null,
    val collectIds: List<Int>? = null
)
