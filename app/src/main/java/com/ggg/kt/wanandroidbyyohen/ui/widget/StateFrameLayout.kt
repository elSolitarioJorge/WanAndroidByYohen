package com.ggg.kt.wanandroidbyyohen.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.children
import com.ggg.kt.wanandroidbyyohen.R
import com.google.android.material.button.MaterialButton

class StateFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    enum class State {
        CONTENT, LOADING, EMPTY, ERROR
    }

    private var currentState = State.CONTENT

    // ViewStub 延迟加载
    private var loadingStub: ViewStub? = null
    private var emptyStub: ViewStub? = null
    private var errorStub: ViewStub? = null

    // 实际加载后的 View
    private var loadingView: View? = null
    private var emptyView: View? = null
    private var errorView: View? = null

    // 内容视图
    private var contentView: View? = null

    // 按钮点击回调
    var onRetryListener: (() -> Unit)? = null
    var onEmptyActionListener: (() -> Unit)? = null

    init {
        // 在 FrameLayout 中添加对应的 ViewStub
        val inflater = LayoutInflater.from(context)

        loadingStub = ViewStub(context).apply {
            layoutResource = R.layout.layout_state_loading
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(this)
        }

        emptyStub = ViewStub(context).apply {
            layoutResource = R.layout.layout_state_empty
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(this)
        }

        errorStub = ViewStub(context).apply {
            layoutResource = R.layout.layout_state_error
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(this)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentView = children.firstOrNull { it !is ViewStub }
    }

    fun showLoading() {
        if (currentState == State.LOADING) return
        currentState = State.LOADING
        if (loadingView == null) {
            loadingView = loadingStub?.inflate()
        }
        hideAll()
        loadingView?.visibility = VISIBLE
    }

    fun showEmpty(
        title: String? = null,
        desc: String? = null,
        btnText: String? = null
    ) {
        currentState = State.EMPTY

        if (emptyView == null) {
            emptyView = emptyStub?.inflate()
            emptyView?.findViewById<MaterialButton>(R.id.btn_empty_action)?.setOnClickListener {
                onEmptyActionListener?.invoke()
            }
        }
        emptyView?.let { view ->
            title?.let { view.findViewById<TextView>(R.id.tv_empty_title).text = it }
            desc?.let { view.findViewById<TextView>(R.id.tv_empty_desc).text = it }

            val btn = view.findViewById<MaterialButton>(R.id.btn_empty_action)
            when (btnText) {
                null -> btn.visibility = View.GONE // 不传隐藏按钮
                else -> {
                    btn.visibility = View.VISIBLE
                    btn.text = btnText
                }
            }
        }
        hideAll()
        emptyView?.visibility = VISIBLE
    }

    fun showError(errorMsg: String? = null) {
        currentState = State.ERROR

        if (errorView == null) {
            errorView = errorStub?.inflate()
            errorView?.findViewById<MaterialButton>(R.id.btn_error_action)?.setOnClickListener {
                onRetryListener?.invoke()
            }
        }
        errorView?.let { view ->
            errorMsg?.let { view.findViewById<TextView>(R.id.tv_error_desc).text = it }
        }
        hideAll()
        errorView?.visibility = VISIBLE
    }

    fun showContent() {
        if (currentState == State.CONTENT) return
        currentState = State.CONTENT
        hideAll()
        contentView?.visibility = VISIBLE
    }

    private fun hideAll() {
        contentView?.visibility = GONE
        loadingView?.visibility = GONE
        emptyView?.visibility = GONE
        errorView?.visibility = GONE
    }
}