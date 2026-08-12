package com.ggg.kt.wanandroidbyyohen.ui.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.ggg.kt.wanandroidbyyohen.common.extension.applyTopBarInsets
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentCollectArticleBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleAdapter
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.launch

class CollectArticleFragment : Fragment() {
    private var _binding: FragmentCollectArticleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollectArticleViewModel by viewModels()

    private val articleAdapter by lazy {
        ArticleAdapter(
            onItemClick = { article ->
                ArticleNavigator.openArticle(requireContext(), article)
            },
            onActionClick = { article ->
                viewModel.uncollect(article)
            },
            actionMode = ArticleAdapter.ArticleActionMode.COLLECTED
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        initRecyclerView()
        initRefresh()
        initLoadMore()
        initClick()
        observeData()
        observeCollectStates()
        observeUncollectMessages()

        if (viewModel.collectArticleState.value !is UiState.Success) {
            viewModel.refresh()
        }
    }

    private fun initClick() {
        binding.stateLayout.onRetryListener = {
            viewModel.refresh()
        }

        binding.stateLayout.onEmptyActionListener = {
            findNavController().navigate(R.id.home_fragment)
        }
    }

    private fun initToolbar() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.layoutToolbar.applyTopBarInsets()
    }

    private fun initRecyclerView() {
        binding.rvArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticles.adapter = articleAdapter
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectArticleState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            if (!binding.swipeRefresh.isRefreshing) {
                                binding.stateLayout.showLoading()
                            }
                        }

                        is UiState.Success -> {
                            binding.swipeRefresh.isRefreshing = false

                            val data = state.data

                            articleAdapter.submitList(data.articles)

                            if (data.articles.isEmpty()) {
                                binding.stateLayout.showEmpty(
                                    title = "这里空空如也",
                                    desc = "你还没有收藏过任何内容\n去发现更多精彩文章吧",
                                    btnText = "去首页逛逛"
                                )
                            } else {
                                binding.stateLayout.showContent()
                            }
                        }

                        is UiState.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            binding.stateLayout.showError()
                        }
                    }
                }
            }
        }
    }

    private fun observeCollectStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.collectStates.collect { states ->
                    articleAdapter.updateCollectStates(states)
                }
            }
        }
    }

    private fun observeUncollectMessages() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.uncollectMessages.collect { message ->
                    Toast.makeText(
                        requireContext(),
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
