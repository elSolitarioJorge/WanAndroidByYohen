package com.ggg.kt.wanandroidbyyohen.data.model

data class SquareTag(
    val title: String,
    val keyword: String? = null
) {
    val key: String
        get() = keyword ?: title
}