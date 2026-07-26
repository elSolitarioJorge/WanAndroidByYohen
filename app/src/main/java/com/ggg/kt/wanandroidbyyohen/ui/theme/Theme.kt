package com.ggg.kt.wanandroidbyyohen.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val WanAndroidLightColorScheme = lightColorScheme(
    // 主色调：用于应用中最重要的 UI 元素，如主要按钮、进度条、选中的开关等
    primary = Blue600,
    // 主色内容：显示在主色元素之上的颜色，如主按钮上的文字或图标
    onPrimary = Color.White,
    // 次要色：用于浮动操作按钮 (FAB)、过滤标签 (Chips) 等次要交互组件
    secondary = Indigo500,
    // 次要色内容：显示在次要色元素之上的颜色
    onSecondary = Color.White,
    // 屏幕背景色：页面的最底层背景，通常是浅灰色或白色
    background = Slate50,
    // 背景内容：页面背景上的默认文字和图标颜色
    onBackground = Slate800,
    // 表面颜色：用于卡片 (Card)、弹窗 (Dialog)、菜单 (Menu) 的底色
    surface = Color.White,
    // 表面内容：显示在卡片、弹窗等表面组件上的文字颜色
    onSurface = Slate800,
    // 错误颜色：用于表达错误状态，如输入框报错提示、删除/警告操作
    error = Red500,
    // 错误内容：显示在错误色背景之上的颜色
    onError = Color.White,
    // 轮廓颜色：用于描边、边框、分割线等，如 OutlinedTextField 的边框
    outline = Slate300
)

@Composable
fun WanAndroidTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WanAndroidLightColorScheme,
        typography = WanAndroidTypography,
        content = content
    )
}

@Preview(
    name = "WanAndroid Theme",
    showBackground = true
)
@Composable
private fun WanAndroidThemePreview() {
    WanAndroidTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "分享文章",
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "分享你发现的优质 Android 技术内容",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "提交分享",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}