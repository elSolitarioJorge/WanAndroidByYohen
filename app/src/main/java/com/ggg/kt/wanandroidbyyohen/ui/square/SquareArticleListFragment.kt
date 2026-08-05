package com.ggg.kt.wanandroidbyyohen.ui.square

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.extension.addLoadMoreListener
import com.ggg.kt.wanandroidbyyohen.data.model.SquareTag
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentSquareArticleListBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleAdapter
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.launch

class SquareArticleListFragment : Fragment() {
    private var _binding: FragmentSquareArticleListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SquareArticleListViewModel by viewModels()

    private val squareTag: SquareTag by lazy {
        SquareTag(
            title = requireArguments().getString(ARG_TITLE).orEmpty(),
            keyword = requireArguments().getString(ARG_KEYWORD)
        )
    }

    private val articleAdapter by lazy {
        ArticleAdapter(
            onItemClick = { article ->
                ArticleNavigator.openArticle(requireContext(), article)
            },
            onActionClick = { article ->
                viewModel.toggleCollect(article)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSquareArticleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTag(squareTag)
        initRecyclerView()
        initRefresh()
        initLoadMore()
        observeData()
        observeCollectState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadIfNeeded()
    }

    private fun initRecyclerView() {
        binding.rvSquareArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSquareArticles.adapter = articleAdapter
    }

    private fun initRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.stateLayout.onRetryListener = {
            viewModel.refresh()
        }
    }

    private fun initLoadMore() {
        binding.rvSquareArticles.addLoadMoreListener {
            viewModel.loadMore()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.squareState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            if (!binding.swipeRefresh.isRefreshing) {
                                binding.stateLayout.showLoading()
                            }
                        }

                        is UiState.Success -> {
                            binding.swipeRefresh.isRefreshing = false
                            articleAdapter.submitList(state.data.articles)
                            binding.stateLayout.showContent()
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

    override fun onDestroyView() {
        binding.rvSquareArticles.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_KEYWORD = "arg_keyword"

        fun newInstance(tag: SquareTag): SquareArticleListFragment {
            return SquareArticleListFragment().apply {
                arguments = bundleOf(
                    ARG_TITLE to tag.title,
                    ARG_KEYWORD to tag.keyword
                )
            }
        }
    }
}
