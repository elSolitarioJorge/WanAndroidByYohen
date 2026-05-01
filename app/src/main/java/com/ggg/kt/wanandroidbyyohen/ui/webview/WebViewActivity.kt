package com.ggg.kt.wanandroidbyyohen.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.ggg.kt.wanandroidbyyohen.common.extension.applyStatusBarPadding
import com.ggg.kt.wanandroidbyyohen.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    private val titleText: String by lazy {
        intent.getStringExtra(EXTRA_TITLE).orEmpty()
    }

    private val url: String by lazy {
        intent.getStringExtra(EXTRA_URL).orEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initWebView()
        loadUrl()
        initBackPressed()
    }

    private fun initToolbar() {
        binding.toolbar.applyStatusBarPadding()
        binding.tvTitle.text = titleText.ifBlank { "文章详情" }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true

        binding.webView.webViewClient = WebViewClient()
    }

    private fun loadUrl() {
        if (url.isNotBlank()) {
            binding.webView.loadUrl(url)
        }
    }

    private fun initBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                finish()
            }
        }
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }


    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_URL = "extra_url"
    }

}