package com.ggg.kt.wanandroidbyyohen.ui.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ShareArticleRoute(
    viewModel: ShareArticleViewModel,
    onBackClick: () -> Unit
) {
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var link by rememberSaveable {
        mutableStateOf("")
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ShareArticleScreen(
        title = title,
        link = link,
        errorMessage = uiState.errorMessage,
        isSubmitting = uiState.isSubmitting,
        onTitleChange = {
            title = it
            viewModel.onInputChanged()
        },
        onLinkChange = {
            link = it
            viewModel.onInputChanged()
        },
        onBackClick = onBackClick,
        onSubmitClick = {
            viewModel.shareArticle(
                title = title,
                link = link
            )
        }
    )
}