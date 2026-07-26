package com.ggg.kt.wanandroidbyyohen.ui.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.repository.ShareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShareArticleUiState(
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isShareSuccessful: Boolean = false
)

class ShareArticleViewModel : ViewModel() {

    private val repository = ShareRepository()
    private val _uiState = MutableStateFlow(
        ShareArticleUiState()
    )
    val uiState: StateFlow<ShareArticleUiState> = _uiState.asStateFlow()
    fun onInputChanged() {
        if (_uiState.value.errorMessage == null) return
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun onShareSuccessHandled() {
        _uiState.update {
            it.copy(isShareSuccessful = false)
        }
    }

    fun shareArticle(title: String, link: String) {
        if (_uiState.value.isSubmitting) return
        val realTitle = title.trim()
        val realLink = link.trim()

        val validationError = when {
            realTitle.isBlank() -> "文章标题不能为空"
            realLink.isBlank() -> "文章链接不能为空"
            !realLink.startsWith("http://") &&
                    !realLink.startsWith("https://") ->
                        "文章链接必须以 http:// 或 https:// 开头"
            else -> null
        }
        if (validationError != null) {
            _uiState.value = ShareArticleUiState(
                errorMessage = validationError
            )
            return
        }
        _uiState.value = ShareArticleUiState(
            isSubmitting = true
        )
        viewModelScope.launch {
            _uiState.value = when (
                val result = repository.shareArticle(
                    title = realTitle,
                    link = realLink
                )
            ) {
                is UiState.Success -> {
                    ShareArticleUiState(
                        isShareSuccessful = true
                    )
                }
                is UiState.Error -> {
                    ShareArticleUiState(
                        errorMessage = result.message
                    )
                }
                is UiState.Loading -> {
                    ShareArticleUiState(
                        isSubmitting = true
                    )
                }
            }
        }
    }
}