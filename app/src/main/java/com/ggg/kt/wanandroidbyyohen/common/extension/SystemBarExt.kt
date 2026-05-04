package com.ggg.kt.wanandroidbyyohen.common.extension

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

fun Activity.setSystemBarsLight(isLight: Boolean) {
    val controller = WindowInsetsControllerCompat(window, window.decorView)

    controller.isAppearanceLightStatusBars = isLight
    controller.isAppearanceLightNavigationBars = isLight
}

fun View.applyTopBarInsets() {
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