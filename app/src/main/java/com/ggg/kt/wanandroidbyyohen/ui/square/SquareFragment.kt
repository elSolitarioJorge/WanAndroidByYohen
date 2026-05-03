package com.ggg.kt.wanandroidbyyohen.ui.square

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.extension.addLoadMoreListener
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentSquareBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleAdapter
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.launch

class SquareFragment : Fragment(R.layout.fragment_square) {
    private var _binding: FragmentSquareBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SquareViewModel by viewModels()
    private val articleAdapter by lazy {
        ArticleAdapter(
            onItemClick = { article ->
                ArticleNavigator.openArticle(requireContext(), article)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSquareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initRefresh()
        initLoadMore()
        initTags()
        observeData()
        viewModel.refreshSquareArticles()
    }

    private fun initRecyclerView() {
        binding.rvSquareArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSquareArticles.adapter = articleAdapter
    }

    private fun initRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshSquareArticles()
        }
    }

    private fun initLoadMore() {
        binding.rvSquareArticles.addLoadMoreListener {
            viewModel.loadMoreSquareArticles()
        }
    }

    private fun initTags() {
        binding.tvLatest.setOnClickListener {
            viewModel.refreshSquareArticles()
        }

        binding.tvInterview.setOnClickListener {
            Toast.makeText(requireContext(), "后续接入搜索：面试", Toast.LENGTH_SHORT).show()
        }

        binding.tvFlutter.setOnClickListener {
            Toast.makeText(requireContext(), "后续接入搜索：Flutter", Toast.LENGTH_SHORT).show()
        }

        binding.tvKotlin.setOnClickListener {
            Toast.makeText(requireContext(), "后续接入搜索：Kotlin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.squareState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            if (!binding.swipeRefresh.isRefreshing) {
                                binding.tvState.visibility = View.VISIBLE
                                binding.tvState.text = "加载中..."
                            }
                        }

                        is UiState.Success -> {
                            binding.tvState.visibility = View.GONE
                            binding.swipeRefresh.isRefreshing = false

                            val squareData = state.data

                            if (squareData.isRefresh) {
                                articleAdapter.submitList(squareData.articles)
                            } else {
                                articleAdapter.addList(squareData.articles)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}