package com.ggg.kt.wanandroidbyyohen.data.model

data class Article(
    val id: Int,
    val title: String,
    val link: String,
    val author: String?,
    val shareUser: String?,
    val niceDate: String?,
    val chapterName: String?,
    val superChapterName: String?,
    val collect: Boolean,
    val fresh: Boolean,
    val envelopePic: String?,
    val desc: String?,
    val isTop: Boolean = false
) {
    fun displayAuthor(): String {
        return when {
            !author.isNullOrBlank() -> author
            !shareUser.isNullOrBlank() -> shareUser
            else -> "匿名"
        }
    }
}
