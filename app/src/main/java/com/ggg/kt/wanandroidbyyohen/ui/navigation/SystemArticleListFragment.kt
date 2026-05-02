package com.ggg.kt.wanandroidbyyohen.ui.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.extension.addLoadMoreListener
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentSystemArticleListBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleAdapter
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.launch

class SystemArticleListFragment : Fragment() {
    private var _binding: FragmentSystemArticleListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SystemArticleListViewModel by viewModels()

    private val articleAdapter by lazy {
        ArticleAdapter { article ->
            ArticleNavigator.openArticle(requireContext(), article)
        }
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

        initToolbar()
        initRecyclerView()
        initRefresh()
        initLoadMore()
        observeData()
        viewModel.refresh()
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
                            if (data.isRefresh) {
                                articleAdapter.submitList(data.articles)
                            } else {
                                articleAdapter.addList(data.articles)
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

    companion object {
        private const val ARG_CID = "arg_cid"
        private const val ARG_CATEGORY_NAME = "arg_category_name"
    }
}
