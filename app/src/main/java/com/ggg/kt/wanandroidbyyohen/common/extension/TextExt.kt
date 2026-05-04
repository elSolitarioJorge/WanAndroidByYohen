package com.ggg.kt.wanandroidbyyohen.common.extension

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.text.HtmlCompat

fun String.toHighlightText(
    highlightColor: Int = Color.RED
): CharSequence {
    val result = SpannableStringBuilder()

    val regex = Regex("<em\\s+class\\s*=\\s*['\"]highlight['\"]\\s*>(.*?)</em>")
    var lastIndex = 0
    regex.findAll(this).forEach { matchResult ->
        val start = matchResult.range.first
        val end = matchResult.range.last + 1
        val normalText = substring(lastIndex, start)
        result.append(normalText.decodeHtml())
        val highlightText = matchResult.groupValues[1].decodeHtml()
        val highlightStart = result.length
        result.append(highlightText)
        val highlightEnd = result.length

        result.setSpan(
            ForegroundColorSpan(highlightColor),
            highlightStart,
            highlightEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        lastIndex = end
    }

    if (lastIndex < length) {
        result.append(substring(lastIndex).decodeHtml())
    }

    return result
}

private fun String.decodeHtml(): String {
    return HtmlCompat.fromHtml(
        this,
        HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString()
}