package com.ggg.kt.wanandroidbyyohen.ui.share

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.ui.theme.Indigo500
import com.ggg.kt.wanandroidbyyohen.ui.theme.Slate200
import com.ggg.kt.wanandroidbyyohen.ui.theme.Slate300
import com.ggg.kt.wanandroidbyyohen.ui.theme.Slate400
import com.ggg.kt.wanandroidbyyohen.ui.theme.Slate500
import com.ggg.kt.wanandroidbyyohen.ui.theme.Slate600
import com.ggg.kt.wanandroidbyyohen.ui.theme.WanAndroidTheme

@Composable
fun ShareArticleScreen(
    title: String,
    link: String,
    errorMessage: String?,
    isSubmitting: Boolean,
    onTitleChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .offset(x = (-16).dp)
                    .size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "返回",
                    modifier = Modifier.size(24.dp),
                    tint = Slate600
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "分享文章",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "分享你发现的优质 Android 技术内容",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate500
            )
            Spacer(modifier = Modifier.height(32.dp))
            ShareArticleInput(
                label = "文章标题",
                value = title,
                onValueChange = onTitleChange,
                placeholder = "请输入文章标题",
                leadingIconRes = R.drawable.ic_text_title,
                enabled = !isSubmitting,
                // 软键盘配置
                keyboardOptions = KeyboardOptions(
                    // 自动首字母大写（按句子模式）
                    capitalization = KeyboardCapitalization.Sentences,
                    // 将键盘右下角的动作按钮设为“下一步”
                    imeAction = ImeAction.Next
                ),
                // 软键盘动作回调
                keyboardActions = KeyboardActions(
                    // 当用户点击键盘上的“下一步”按钮时执行的操作
                    onNext = {
                        // 将焦点移动到下方的输入框
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            ShareArticleInput(
                label = "文章链接",
                value = link,
                onValueChange = onLinkChange,
                placeholder = "例如 https://www.wanandroid.com",
                leadingIconRes = R.drawable.ic_link,
                enabled = !isSubmitting,
                keyboardOptions = KeyboardOptions(
                    // 键盘类型设为 URI，方便输入网址（键盘通常会直接显示 .com 等符号）
                    keyboardType = KeyboardType.Uri,
                    // 将键盘右下角的动作按钮设为“完成”
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    // 当用户点击键盘上的“完成”按钮时执行的操作
                    onDone = {
                        // 收起键盘
                        focusManager.clearFocus()
                        // 如果当前不是正在提交状态，则触发提交逻辑
                        if (!isSubmitting) {
                            onSubmitClick()
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage.orEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 20.dp),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSubmitClick()
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Indigo500,
                    contentColor = Color.White,
                    disabledContainerColor = Slate300,
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = if (isSubmitting) "正在提交..." else "提交分享",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ShareArticleInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    @DrawableRes leadingIconRes: Int,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions
) {
    Text(
        text = label,
        modifier = Modifier.padding(start = 4.dp),
        style = MaterialTheme.typography.titleSmall,
        color = Slate600
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        enabled = enabled,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(leadingIconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            // 获取焦点时的输入框背景色
            focusedContainerColor = Color.White,
            // 未获取焦点时的输入框背景色
            unfocusedContainerColor = Color.White,
            // 禁用状态下的输入框背景色
            disabledContainerColor = Color.White,
            // 获取焦点时的边框颜色
            focusedBorderColor = Indigo500,
            // 未获取焦点时的边框颜色（默认状态）
            unfocusedBorderColor = Slate200,
            // 禁用状态下的边框颜色
            disabledBorderColor = Slate200,
            // 获取焦点时的正文文字颜色
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            // 未获取焦点时的正文文字颜色
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            // 获取焦点时的左侧图标颜色
            focusedLeadingIconColor = Slate400,
            // 未获取焦点时的左侧图标颜色
            unfocusedLeadingIconColor = Slate400,
            // 获取焦点时的占位符（提示）文字颜色
            focusedPlaceholderColor = Slate400,
            // 未获取焦点时的占位符（提示）文字颜色
            unfocusedPlaceholderColor = Slate400,
            // 光标颜色
            cursorColor = Indigo500
        )
    )
}

@Preview(
    name = "分享文章",
    showBackground = true
)
@Composable
private fun ShareArticleScreenPreview() {
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var link by rememberSaveable {
        mutableStateOf("")
    }
    var errorMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    WanAndroidTheme {
        ShareArticleScreen(
            title = title,
            link = link,
            errorMessage = errorMessage,
            isSubmitting = false,
            onTitleChange = {
                title = it
                errorMessage = null
            },
            onLinkChange = {
                link = it
                errorMessage = null
            },
            onBackClick = {},
            onSubmitClick = {
                errorMessage = when {
                    title.isBlank() -> "文章标题不能为空"
                    link.isBlank() -> "文章链接不能为空"
                    else -> null
                }
            }
        )
    }
}