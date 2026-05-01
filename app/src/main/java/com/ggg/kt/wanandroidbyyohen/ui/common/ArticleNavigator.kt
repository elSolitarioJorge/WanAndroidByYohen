package com.ggg.kt.wanandroidbyyohen.ui.common

import android.content.Context
import android.content.Intent
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.ui.webview.WebViewActivity

object ArticleNavigator {
    fun openArticle(context: Context, article: Article) {
        openWebView(
            context = context,
            title = article.title,
            url = article.link
        )
    }

    fun openWebView(
        context: Context,
        title: String,
        url: String
    ) {
        val intent = Intent(context, WebViewActivity::class.java).apply {
            putExtra(WebViewActivity.EXTRA_TITLE, title)
            putExtra(WebViewActivity.EXTRA_URL, url)
        }
        context.startActivity(intent)
    }
}