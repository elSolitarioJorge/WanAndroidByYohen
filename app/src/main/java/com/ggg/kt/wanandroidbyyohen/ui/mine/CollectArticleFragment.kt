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
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.extension.addLoadMoreListener
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
            onCollectClick = { article ->
                viewModel.uncollect(article)
            }
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
        observeData()
        observeUncollect()

        viewModel.refresh()
    }

    private fun initToolbar() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
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
                                binding.tvState.visibility = View.VISIBLE
                                binding.tvState.text = "加载中..."
                            }
                        }

                        is UiState.Success -> {
                            binding.swipeRefresh.isRefreshing = false
                            binding.tvState.visibility = View.GONE

                            val data = state.data

                            if (data.isRefresh) {
                                articleAdapter.submitList(data.articles)
                            } else {
                                articleAdapter.addList(data.articles)
                            }

                            if (data.isRefresh && data.articles.isEmpty()) {
                                binding.tvState.visibility = View.VISIBLE
                                binding.tvState.text = "暂无收藏"
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

    private fun observeUncollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uncollectState.collect { state ->
                    when (state) {
                        null -> Unit
                        is UiState.Loading -> Unit

                        is UiState.Success -> {
                            articleAdapter.removeArticle(state.data.id)
                            Toast.makeText(
                                requireContext(),
                                "已取消收藏",
                                Toast.LENGTH_SHORT
                            ).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}