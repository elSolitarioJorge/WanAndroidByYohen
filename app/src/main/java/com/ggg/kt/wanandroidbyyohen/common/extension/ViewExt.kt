package com.ggg.kt.wanandroidbyyohen.common.extension

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

fun View.applyStatusBarPadding() {
    val originalHeight = layoutParams.height
    val originalPaddingTop = paddingTop

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

        view.updatePadding(
            top = originalPaddingTop + statusBarHeight
        )

        if (originalHeight > 0) {
            view.updateLayoutParams<ViewGroup.LayoutParams> {
                height = originalHeight + statusBarHeight
            }
        }

        insets
    }

    ViewCompat.requestApplyInsets(this)
}