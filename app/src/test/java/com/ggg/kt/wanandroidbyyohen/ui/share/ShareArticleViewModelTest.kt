package com.ggg.kt.wanandroidbyyohen.ui.share

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShareArticleViewModelTest {

    @Test
    fun blankTitleShowsError() {
        val viewModel = ShareArticleViewModel()

        viewModel.shareArticle(
            title = "   ",
            link = "https://www.wanandroid.com"
        )

        assertEquals(
            "文章标题不能为空",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun blankLinkShowsError() {
        val viewModel = ShareArticleViewModel()

        viewModel.shareArticle(
            title = "测试文章",
            link = "   "
        )

        assertEquals(
            "文章链接不能为空",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun invalidLinkShowsError() {
        val viewModel = ShareArticleViewModel()

        viewModel.shareArticle(
            title = "测试文章",
            link = "www.wanandroid.com"
        )

        assertEquals(
            "文章链接必须以 http:// 或 https:// 开头",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun inputChangeClearsError() {
        val viewModel = ShareArticleViewModel()

        viewModel.shareArticle(
            title = "",
            link = ""
        )

        viewModel.onInputChanged()

        assertNull(
            viewModel.uiState.value.errorMessage
        )
    }
}