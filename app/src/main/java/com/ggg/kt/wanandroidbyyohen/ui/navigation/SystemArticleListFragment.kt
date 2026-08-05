package com.ggg.kt.wanandroidbyyohen.ui.navigation

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
import com.ggg.kt.wanandroidbyyohen.common.extension.applyTopBarInsets
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentSystemArticleListBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleAdapter
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.launch

class SystemArticleListFragment : Fragment() {
    private var _binding: FragmentSystemArticleListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SystemArticleListViewModel by viewModels()

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

    private val cid: Int by lazy {
        requireArguments().getInt(ARG_CID)
    }

    private val categoryName: String by lazy {
        requireArguments().getString(ARG_CATEGORY_NAME).orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSystemArticleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setCid(cid)

        initInsets()
        initToolbar()
        initRecyclerView()
        initRefresh()
        initLoadMore()
        observeData()
        observeCollectState()
        if (viewModel.articleState.value !is UiState.Success) {
            viewModel.refresh()
        }
    }

    private fun initInsets() {
        binding.contentGroup.applyTopBarInsets()
    }

    private fun initToolbar() {
        binding.tvTitle.text = categoryName.ifBlank { "体系文章" }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
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
                viewModel.articleState.collect { state ->
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
                            articleAdapter.submitList(data.articles)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CID = "arg_cid"
        private const val ARG_CATEGORY_NAME = "arg_category_name"
    }
}
