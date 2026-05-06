package com.ggg.kt.wanandroidbyyohen.data.model

sealed class MineUiState {
    data object Loading : MineUiState()

    data object LoggedOut : MineUiState()

    data class Content(
        val userInfoData: UserInfoData,
        val isFromLocal: Boolean
    ) : MineUiState()

    data class Error(
        val message: String
    ) : MineUiState()
}
