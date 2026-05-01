package com.ggg.kt.wanandroidbyyohen.data.model

data class Chapter(
    val id: Int,
    val name: String,
    val children: List<Chapter>?
)
