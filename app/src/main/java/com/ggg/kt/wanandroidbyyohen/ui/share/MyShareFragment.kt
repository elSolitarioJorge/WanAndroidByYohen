package com.ggg.kt.wanandroidbyyohen.ui.share

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
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentMyShareBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleAdapter
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.launch

class MyShareFragment : Fragment() {

    private var _binding: FragmentMyShareBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyShareViewModel by viewModels()

    private val articleAdapter by lazy {
        ArticleAdapter(
            onItemClick = { article ->
                ArticleNavigator.openArticle(requireContext(), article)
            },
            onActionClick = { article ->
                viewModel.deleteArticle(article)
            },
            actionMode = ArticleAdapter.ArticleActionMode.SHARED
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyShareBinding.inflate(inflater, container, false)
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
        observeDelete()

        if (viewModel.myShareState.value !is UiState.Success) {
            viewModel.refresh()
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

    private fun initClick() {
        binding.stateLayout.onRetryListener = {
            viewModel.refresh()
        }
        binding.stateLayout.onEmptyActionListener = {
            findNavController().navigate(R.id.share_article_fragment)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myShareState.collect { state ->
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

                            if (data.isRefresh && data.articles.isEmpty()) {
                                binding.stateLayout.showEmpty(
                                    title = "这里空空如也",
                                    desc = "你还没有分享过任何内容",
                                    btnText = "去分享"
                                )
                            } else {
                                binding.stateLayout.showContent()
                            }
                        }

                        is UiState.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            binding.stateLayout.showError(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun observeDelete() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteState.collect { state ->
                    when (state) {
                        null -> Unit
                        is UiState.Loading -> Unit

                        is UiState.Success -> {
                            articleAdapter.removeArticle(state.data.id)
                            Toast.makeText(
                                requireContext(),
                                "删除成功",
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
