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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Navigation
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentNavigationBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleAdapter
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.launch

class NavigationFragment : Fragment(R.layout.fragment_navigation) {
    private var _binding: FragmentNavigationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NavigationViewModel by viewModels()

    private var navigationList: List<Navigation> = emptyList()

    private val categoryAdapter by lazy {
        NavigationCategoryAdapter { position ->
            selectCategory(position)
        }
    }

    private val articleAdapter by lazy {
        ArticleAdapter { article ->
            ArticleNavigator.openArticle(requireContext(), article)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNavigationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        observeData()

        viewModel.loadNavigationList()
    }

    private fun initRecyclerView() {
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = categoryAdapter

        binding.rvArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticles.adapter = articleAdapter
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.contentGroup.visibility = View.GONE
                            binding.tvState.visibility = View.VISIBLE
                            binding.tvState.text = "加载中..."
                        }

                        is UiState.Success -> {
                            binding.tvState.visibility = View.GONE
                            binding.contentGroup.visibility = View.VISIBLE

                            navigationList = state.data
                            categoryAdapter.submitList(navigationList)

                            if (navigationList.isNotEmpty()) {
                                selectCategory(0)
                            }
                        }

                        is UiState.Error -> {
                            binding.contentGroup.visibility = View.GONE
                            binding.tvState.visibility = View.VISIBLE
                            binding.tvState.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun selectCategory(position: Int) {
        if (position !in navigationList.indices) return

        categoryAdapter.select(position)
        articleAdapter.submitList(navigationList[position].articles)
        binding.rvArticles.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}