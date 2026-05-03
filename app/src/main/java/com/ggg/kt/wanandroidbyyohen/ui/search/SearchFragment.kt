package com.ggg.kt.wanandroidbyyohen.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.extension.addLoadMoreListener
import com.ggg.kt.wanandroidbyyohen.data.local.UserStore
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentSearchBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleAdapter
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private val hotKeyAdapter by lazy {
        HotKeyAdapter { hotKey ->
            binding.etKeyword.setText(hotKey.name)
            binding.etKeyword.setSelection(hotKey.name.length)
            viewModel.search(hotKey.name)
        }
    }

    private val articleAdapter by lazy {
        ArticleAdapter(
            onItemClick = { article ->
                ArticleNavigator.openArticle(requireContext(), article)
            },
            onCollectClick = { article ->
                handleCollectClick(article)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initClick()
        initRefresh()
        initLoadMore()
        observeData()

        viewModel.loadHotKeys()
    }

    private fun initRecyclerView() {
        binding.rvHotKeys.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        binding.rvHotKeys.adapter = hotKeyAdapter

        binding.rvArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticles.adapter = articleAdapter
    }

    private fun initClick() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvSearch.setOnClickListener {
            viewModel.search(binding.etKeyword.text.toString())
        }

        binding.etKeyword.setOnEditorActionListener { _, actionId, event ->
            val isSearchAction = actionId == EditorInfo.IME_ACTION_SEARCH
            val isEnterUp = event?.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_UP

            if (isSearchAction || isEnterUp) {
                viewModel.search(binding.etKeyword.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun initRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun initLoadMore() {
        binding.rvArticles.addLoadMoreListener {
            viewModel.loadMore()
        }
    }

    private fun observeData() {
        observeHotKeys()
        observeSearchResult()
        observeCollectState()
    }

    private fun observeHotKeys() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.hotKeyState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.tvState.visibility = View.VISIBLE
                            binding.tvState.text = "加载中..."
                        }

                        is UiState.Success -> {
                            binding.tvState.visibility = View.GONE
                            hotKeyAdapter.submitList(state.data)
                        }

                        is UiState.Error -> {
                            binding.tvState.visibility = View.VISIBLE
                            binding.tvState.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun observeSearchResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchState.collect { state ->
                    when (state) {
                        null -> Unit

                        is UiState.Loading -> {
                            binding.layoutHotKey.visibility = View.GONE
                            binding.swipeRefresh.visibility = View.VISIBLE

                            if (!binding.swipeRefresh.isRefreshing) {
                                binding.tvState.visibility = View.VISIBLE
                                binding.tvState.text = "搜索中..."
                            }
                        }

                        is UiState.Success -> {
                            binding.swipeRefresh.isRefreshing = false
                            binding.tvState.visibility = View.GONE
                            binding.layoutHotKey.visibility = View.GONE
                            binding.swipeRefresh.visibility = View.VISIBLE

                            val data = state.data

                            if (data.isRefresh) {
                                articleAdapter.submitList(data.articles)
                            } else {
                                articleAdapter.addList(data.articles)
                            }

                            if (data.isRefresh && data.articles.isEmpty()) {
                                binding.tvState.visibility = View.VISIBLE
                                binding.tvState.text = "暂无搜索结果"
                            }
                        }

                        is UiState.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            binding.tvState.visibility = View.VISIBLE
                            binding.tvState.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun observeCollectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectState.collect { state ->
                    when (state) {
                        null -> Unit
                        is UiState.Loading -> Unit

                        is UiState.Success -> {
                            val articleId = state.data.first
                            val collect = state.data.second
                            articleAdapter.updateCollectState(articleId, collect)
                        }

                        is UiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun handleCollectClick(article: Article) {
        if (!UserStore.isLogin()) {
            findNavController().navigate(R.id.login_fragment)
            return
        }

        viewModel.toggleCollect(article)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}