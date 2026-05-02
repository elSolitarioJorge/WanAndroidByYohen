package com.ggg.kt.wanandroidbyyohen.ui.navigation

import android.graphics.Typeface
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
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.Chapter
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentNavigationBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.launch

class NavigationFragment : Fragment() {
    private var _binding: FragmentNavigationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NavigationViewModel by viewModels()

    private var currentMode = PageMode.NAVIGATION
    private var navigationSections: List<SectionUi<Article>> = emptyList()
    private var systemSections: List<SectionUi<Chapter>> = emptyList()

    private var isProgrammaticScroll = true

    private val navigationCategoryAdapter by lazy {
        SideCategoryAdapter<SectionUi<Article>>(
            getTitle = { it.title },
            onItemClick = { position ->
                onCategoryClick(position)
            }
        )
    }

    private val systemCategoryAdapter by lazy {
        SideCategoryAdapter<SectionUi<Chapter>>(
            getTitle = { it.title },
            onItemClick = { position ->
                onCategoryClick(position)
            }
        )
    }

    private val navigationSectionAdapter by lazy {
        SectionAdapter<Article>(
            getItemName = { it.title },
            onItemClick = { article ->
                ArticleNavigator.openArticle(requireContext(), article)
            }
        )
    }

    private val systemSectionAdapter by lazy {
        SectionAdapter<Chapter>(
            getItemName = { it.name },
            onItemClick = { chapter ->
                openSystemArticles(chapter)
            }
        )
    }

    private val sectionLayoutManager by lazy {
        LinearLayoutManager(requireContext())
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
        initTopTabs()
        initSectionScrollListener()
        observeData()
        switchMode(PageMode.NAVIGATION)
    }

    private fun initRecyclerView() {
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSections.layoutManager = sectionLayoutManager
    }

    private fun initTopTabs() {
        binding.tvNavigationTab.setOnClickListener {
            switchMode(PageMode.NAVIGATION)
        }

        binding.tvSystemTab.setOnClickListener {
            switchMode(PageMode.SYSTEM)
        }
    }

    private fun initSectionScrollListener() {
        binding.rvSections.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isProgrammaticScroll = false
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isProgrammaticScroll) return

                val firstVisiblePosition = sectionLayoutManager.findFirstVisibleItemPosition()
                if (firstVisiblePosition == RecyclerView.NO_POSITION) return

                when (currentMode) {
                    PageMode.NAVIGATION -> {
                        navigationCategoryAdapter.select(firstVisiblePosition)
                    }

                    PageMode.SYSTEM -> {
                        systemCategoryAdapter.select(firstVisiblePosition)
                    }
                }
            }
        })
    }

    private fun observeData() {
        observeNavigationData()
        observeSystemData()
    }

    private fun observeNavigationData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationState.collect { state ->
                    if (currentMode != PageMode.NAVIGATION) return@collect

                    when (state) {
                        is UiState.Loading -> showLoading()

                        is UiState.Success -> {
                            navigationSections = state.data.map {
                                SectionUi(
                                    id = it.cid,
                                    title =  it.name,
                                    items = it.articles
                                )
                            }
                            showContent()
                            navigationCategoryAdapter.submitList(navigationSections)
                            navigationSectionAdapter.submitList(navigationSections)
                            selectCategory(0)
                        }

                        is UiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    private fun observeSystemData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.systemState.collect { state ->
                    if (currentMode != PageMode.SYSTEM) return@collect

                    when (state) {
                        is UiState.Loading -> showLoading()

                        is UiState.Success -> {
                            systemSections = state.data.map {
                                SectionUi(
                                    id = it.id,
                                    title = it.name,
                                    items = it.children.orEmpty()
                                )
                            }
                            showContent()
                            systemCategoryAdapter.submitList(systemSections)
                            systemSectionAdapter.submitList(systemSections)
                            selectCategory(0)
                        }

                        is UiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    private fun switchMode(mode: PageMode) {
        currentMode = mode
        updateTopTabStyle(mode)

        when (mode) {
            PageMode.NAVIGATION -> {
                binding.rvCategories.adapter = navigationCategoryAdapter
                binding.rvSections.adapter = navigationSectionAdapter

                if (navigationSections.isEmpty()) {
                    viewModel.loadNavigationList()
                } else {
                    showContent()
                    navigationCategoryAdapter.submitList(navigationSections)
                    navigationSectionAdapter.submitList(navigationSections)
                    selectCategory(0)
                }
            }

            PageMode.SYSTEM -> {
                binding.rvCategories.adapter = systemCategoryAdapter
                binding.rvSections.adapter = systemSectionAdapter
                if (systemSections.isEmpty()) {
                    viewModel.loadSystemTree()
                } else {
                    showContent()
                    systemCategoryAdapter.submitList(systemSections)
                    systemSectionAdapter.submitList(systemSections)
                    selectCategory(0)
                }
            }
        }
    }

    private fun updateTopTabStyle(mode: PageMode) {
        val activeColor = 0xFF5BAEDB.toInt()
        val inactiveColor = 0xFF9ACBE5.toInt()

        if (mode == PageMode.NAVIGATION) {
            binding.tvNavigationTab.setTextColor(activeColor)
            binding.tvNavigationTab.setTypeface(null, Typeface.BOLD)

            binding.tvSystemTab.setTextColor(inactiveColor)
            binding.tvSystemTab.setTypeface(null, Typeface.BOLD)
        } else {
            binding.tvSystemTab.setTextColor(activeColor)
            binding.tvSystemTab.setTypeface(null, Typeface.BOLD)

            binding.tvNavigationTab.setTextColor(inactiveColor)
            binding.tvNavigationTab.setTypeface(null, Typeface.BOLD)
        }
    }

    private fun onCategoryClick(position: Int) {
        selectCategory(position)
        isProgrammaticScroll = true
        binding.rvSections.smoothScrollToPosition(position)
    }

    private fun selectCategory(position: Int) {
        when (currentMode) {
            PageMode.NAVIGATION -> {
                if (position !in navigationSections.indices) return
                navigationCategoryAdapter.select(position)
            }

            PageMode.SYSTEM -> {
                if (position !in systemSections.indices) return
                systemCategoryAdapter.select(position)
            }
        }
        binding.rvCategories.smoothScrollToPosition(position)
    }

    private fun openSystemArticles(chapter: Chapter) {
        Toast.makeText(
            requireContext(),
            "点击体系：${chapter.name}, id=${chapter.id}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showLoading() {
        binding.contentGroup.visibility = View.GONE
        binding.tvState.visibility = View.VISIBLE
        binding.tvState.text = "加载中..."
    }

    private fun showContent() {
        binding.tvState.visibility = View.GONE
        binding.contentGroup.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.contentGroup.visibility = View.GONE
        binding.tvState.visibility = View.VISIBLE
        binding.tvState.text = message
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private enum class PageMode {
        NAVIGATION,
        SYSTEM
    }
}