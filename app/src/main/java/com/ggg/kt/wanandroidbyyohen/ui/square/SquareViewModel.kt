package com.ggg.kt.wanandroidbyyohen.ui.square

import androidx.lifecycle.ViewModel
import com.ggg.kt.wanandroidbyyohen.data.model.SquareTag

class SquareViewModel : ViewModel() {
    val tags = listOf(
        SquareTag(title = "最新分享"),
        SquareTag(title = "面试", keyword = "面试"),
        SquareTag(title = "Kotlin", keyword = "Kotlin"),
        SquareTag(title = "Java", keyword = "Java"),
        SquareTag(title = "性能优化", keyword = "性能优化"),
        SquareTag(title = "源码", keyword = "源码"),
        SquareTag(title = "架构", keyword = "架构"),
        SquareTag(title = "Jetpack", keyword = "Jetpack"),
        SquareTag(title = "开源", keyword = "开源")
    )

    private var selectedPosition = 0

    fun selectPosition(position: Int) {
        if (position !in tags.indices) return

        selectedPosition = position
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun getSelectedTag(): SquareTag {
        return tags[selectedPosition]
    }
}
