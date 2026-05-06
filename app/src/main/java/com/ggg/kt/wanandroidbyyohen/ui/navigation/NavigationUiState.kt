package com.ggg.kt.wanandroidbyyohen.ui.navigation

enum class NavigationPageMode {
    NAVIGATION,
    SYSTEM
}

data class NavigationScrollState(
    val position: Int = 0,
    val offset: Int = 0
)