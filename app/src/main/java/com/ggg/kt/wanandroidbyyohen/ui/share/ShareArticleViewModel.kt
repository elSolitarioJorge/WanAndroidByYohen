package com.ggg.kt.wanandroidbyyohen.ui.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.repository.ShareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShareArticleViewModel : ViewModel() {

    private val repository = ShareRepository()

    private val _shareState = MutableStateFlow<UiState<Any>?>(null)
    val shareState: StateFlow<UiState<Any>?> = _shareState

    fun shareArticle(title: String, link: String) {
        val realTitle = title.trim()
        val realLink = link.trim()

        if (realTitle.isBlank()) {
            _shareState.value = UiState.Error("文章标题不能为空")
            return
        }

        if (realLink.isBlank()) {
            _shareState.value = UiState.Error("文章链接不能为空")
            return
        }

        if (!realLink.startsWith("http://") && !realLink.startsWith("https://")) {
            _shareState.value = UiState.Error("文章链接必须以 http:// 或 https:// 开头")
            return
        }

        viewModelScope.launch {
            _shareState.value = UiState.Loading
            _shareState.value = repository.shareArticle(realTitle, realLink)
        }
    }
}