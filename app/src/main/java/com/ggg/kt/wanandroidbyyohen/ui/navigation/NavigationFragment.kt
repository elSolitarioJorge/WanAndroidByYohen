package com.ggg.kt.wanandroidbyyohen.ui.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.extension.applyTopBarInsets
import com.ggg.kt.wanandroidbyyohen.data.model.Article
import com.ggg.kt.wanandroidbyyohen.data.model.Chapter
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentNavigationBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.launch

class NavigationFragment : Fragment() {
    private var _binding: FragmentNavigationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NavigationViewModel by viewModels()
    private val currentMode get() = viewModel.currentMode

    private var suppressSectionScrollSync = false

    private val categoryLayoutManager by lazy {
        LinearLayoutManager(requireContext())
    }

    private val sectionLayoutManager by lazy {
        LinearLayoutManager(requireContext())
    }

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

        initInsets()
        initRecyclerView()
        initTopTabs()
        initStateActions()
        initSectionScrollListener()
        observeData()
        switchMode(viewModel.currentMode, force = true)
    }

    private fun initInsets() {
        binding.layoutTopTabs.applyTopBarInsets()
    }

    private fun initRecyclerView() {
        binding.rvCategories.layoutManager = categoryLayoutManager
        binding.rvCategories.itemAnimator = null
        binding.rvSections.layoutManager = sectionLayoutManager
    }

    private fun initTopTabs() {
        binding.tvNavigationTab.setOnClickListener {
            switchMode(NavigationPageMode.NAVIGATION)
        }

        binding.tvSystemTab.setOnClickListener {
            switchMode(NavigationPageMode.SYSTEM)
        }
    }

    private fun initStateActions() {
        binding.stateLayout.onRetryListener = {
            when (currentMode) {
                NavigationPageMode.NAVIGATION -> viewModel.refreshNavigationList()
                NavigationPageMode.SYSTEM -> viewModel.refreshSystemTree()
            }
        }
    }

    private fun initSectionScrollListener() {
        binding.rvSections.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (
                    newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !suppressSectionScrollSync
                ) {
                    ensureCategoryVisible(viewModel.getSelectedPosition(currentMode))
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisiblePosition = sectionLayoutManager.findFirstVisibleItemPosition()
                if (firstVisiblePosition == RecyclerView.NO_POSITION) return

                val firstView = sectionLayoutManager.findViewByPosition(firstVisiblePosition)
                viewModel.saveScrollState(
                    mode = currentMode,
                    position = firstVisiblePosition,
                    offset = firstView?.top ?: 0
                )
                updateStickyHeader(firstVisiblePosition)

                if (suppressSectionScrollSync) return

                if (viewModel.getSelectedPosition(currentMode) != firstVisiblePosition) {
                    updateSelectedCategory(
                        position = firstVisiblePosition,
                        keepCategoryVisible = true
                    )
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
                    if (currentMode == NavigationPageMode.NAVIGATION) {
                        renderNavigationState(state)
                    }
                }
            }
        }
    }

    private fun observeSystemData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.systemState.collect { state ->
                    if (currentMode == NavigationPageMode.SYSTEM) {
                        renderSystemState(state)
                    }
                }
            }
        }
    }

    private fun switchMode(
        mode: NavigationPageMode,
        force: Boolean = false
    ) {
        val previousMode = currentMode
        if (!force && previousMode == mode) return

        if (!force) {
            saveCurrentScrollState(previousMode)
        }

        viewModel.setCurrentMode(mode)
        updateTopTabStyle(mode)
        bindAdapters(mode)
        renderCurrentState(mode)

        when (mode) {
            NavigationPageMode.NAVIGATION -> viewModel.loadNavigationListIfNeeded()
            NavigationPageMode.SYSTEM -> viewModel.loadSystemTreeIfNeeded()
        }
    }

    private fun bindAdapters(mode: NavigationPageMode) {
        when (mode) {
            NavigationPageMode.NAVIGATION -> {
                binding.rvCategories.adapter = navigationCategoryAdapter
                binding.rvSections.adapter = navigationSectionAdapter
            }

            NavigationPageMode.SYSTEM -> {
                binding.rvCategories.adapter = systemCategoryAdapter
                binding.rvSections.adapter = systemSectionAdapter
            }
        }
    }

    private fun renderCurrentState(mode: NavigationPageMode) {
        when (mode) {
            NavigationPageMode.NAVIGATION -> renderNavigationState(viewModel.navigationState.value)
            NavigationPageMode.SYSTEM -> renderSystemState(viewModel.systemState.value)
        }
    }

    private fun renderNavigationState(state: UiState<List<SectionUi<Article>>>) {
        when (state) {
            is UiState.Loading -> showLoading()
            is UiState.Success -> renderContent(
                sections = state.data,
                categoryAdapter = navigationCategoryAdapter,
                sectionAdapter = navigationSectionAdapter
            )

            is UiState.Error -> showError(state.message)
        }
    }

    private fun renderSystemState(state: UiState<List<SectionUi<Chapter>>>) {
        when (state) {
            is UiState.Loading -> showLoading()
            is UiState.Success -> renderContent(
                sections = state.data,
                categoryAdapter = systemCategoryAdapter,
                sectionAdapter = systemSectionAdapter
            )

            is UiState.Error -> showError(state.message)
        }
    }

    private fun <T> renderContent(
        sections: List<SectionUi<T>>,
        categoryAdapter: SideCategoryAdapter<SectionUi<T>>,
        sectionAdapter: SectionAdapter<T>
    ) {
        if (sections.isEmpty()) {
            showEmpty()
            return
        }

        showContent()
        val selectedPosition = viewModel.getSelectedPosition(currentMode)
        categoryAdapter.submitList(sections, selectedPosition)
        sectionAdapter.submitList(sections)
        restoreSavedListState(sections.size)
    }

    private fun restoreSavedListState(sectionCount: Int) {
        if (sectionCount == 0) {
            hideStickyHeader()
            return
        }

        val mode = currentMode
        val selectedPosition = viewModel.getSelectedPosition(mode)
        val scrollState = viewModel.getScrollState(mode)

        updateSelectedCategory(
            position = selectedPosition,
            keepCategoryVisible = false
        )
        categoryLayoutManager.scrollToPositionWithOffset(selectedPosition, 0)

        suppressSectionScrollSync = true
        binding.rvSections.post {
            val currentBinding = _binding
            if (currentBinding != null && currentMode == mode) {
                sectionLayoutManager.scrollToPositionWithOffset(
                    scrollState.position,
                    scrollState.offset
                )
                updateStickyHeader(scrollState.position)

                currentBinding.rvSections.post {
                    if (_binding != null) {
                        suppressSectionScrollSync = false
                        updateStickyHeader()
                    }
                }
            }
        }
    }

    private fun updateTopTabStyle(mode: NavigationPageMode) {
        binding.tvNavigationTab.isSelected = mode == NavigationPageMode.NAVIGATION
        binding.tvSystemTab.isSelected = mode == NavigationPageMode.SYSTEM
    }

    private fun onCategoryClick(position: Int) {
        if (position !in 0 until viewModel.getSectionCount(currentMode)) return

        updateSelectedCategory(
            position = position,
            keepCategoryVisible = true
        )

        suppressSectionScrollSync = true
        sectionLayoutManager.scrollToPositionWithOffset(position, 0)
        viewModel.saveScrollState(
            mode = currentMode,
            position = position,
            offset = 0
        )
        updateStickyHeader(position)

        binding.rvSections.postDelayed(
            {
                if (_binding != null) {
                    suppressSectionScrollSync = false
                    updateStickyHeader()
                }
            },
            CATEGORY_CLICK_SCROLL_SUPPRESS_MS
        )
    }

    private fun updateSelectedCategory(
        position: Int,
        keepCategoryVisible: Boolean
    ) {
        if (position !in 0 until viewModel.getSectionCount(currentMode)) return

        viewModel.saveSelectedPosition(currentMode, position)
        when (currentMode) {
            NavigationPageMode.NAVIGATION -> {
                navigationCategoryAdapter.select(position, binding.rvCategories)
            }

            NavigationPageMode.SYSTEM -> {
                systemCategoryAdapter.select(position, binding.rvCategories)
            }
        }

        if (keepCategoryVisible) {
            ensureCategoryVisible(position)
        }
    }

    private fun ensureCategoryVisible(position: Int) {
        val firstVisiblePosition = categoryLayoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = categoryLayoutManager.findLastVisibleItemPosition()
        if (
            firstVisiblePosition == RecyclerView.NO_POSITION ||
            lastVisiblePosition == RecyclerView.NO_POSITION
        ) {
            categoryLayoutManager.scrollToPositionWithOffset(position, 0)
            return
        }

        val visibleWindowSize = lastVisiblePosition - firstVisiblePosition
        val targetPosition = if (position < firstVisiblePosition) {
            position
        } else if (position > lastVisiblePosition) {
            (position - visibleWindowSize).coerceAtLeast(0)
        } else {
            return
        }
        categoryLayoutManager.scrollToPositionWithOffset(targetPosition, 0)
    }

    private fun updateStickyHeader(
        firstVisiblePosition: Int = sectionLayoutManager.findFirstVisibleItemPosition()
    ) {
        if (firstVisiblePosition == RecyclerView.NO_POSITION) {
            hideStickyHeader()
            return
        }

        val title = viewModel.getSectionTitle(currentMode, firstVisiblePosition)
        if (title == null) {
            hideStickyHeader()
            return
        }

        binding.layoutStickyHeader.visibility = View.VISIBLE
        binding.tvStickyHeaderTitle.text = title

        val nextView = sectionLayoutManager.findViewByPosition(firstVisiblePosition + 1)
        val stickyHeaderHeight = binding.layoutStickyHeader.height
        binding.layoutStickyHeader.translationY = if (
            nextView != null &&
            stickyHeaderHeight > 0 &&
            nextView.top < stickyHeaderHeight
        ) {
            (nextView.top - stickyHeaderHeight).toFloat()
        } else {
            0f
        }
    }

    private fun hideStickyHeader() {
        binding.layoutStickyHeader.visibility = View.GONE
        binding.layoutStickyHeader.translationY = 0f
    }

    private fun saveCurrentScrollState(mode: NavigationPageMode = currentMode) {
        if (viewModel.getSectionCount(mode) == 0) return

        val position = sectionLayoutManager.findFirstVisibleItemPosition()
        if (position == RecyclerView.NO_POSITION) return

        viewModel.saveScrollState(
            mode = mode,
            position = position,
            offset = sectionLayoutManager.findViewByPosition(position)?.top ?: 0
        )
    }

    private fun openSystemArticles(chapter: Chapter) {
        findNavController().navigate(
            R.id.system_article_list_fragment,
            bundleOf(
                "arg_cid" to chapter.id,
                "arg_category_name" to chapter.name
            )
        )
    }

    private fun showLoading() {
        hideStickyHeader()
        binding.stateLayout.showLoading()
    }

    private fun showContent() {
        binding.stateLayout.showContent()
    }

    private fun showEmpty() {
        hideStickyHeader()
        binding.stateLayout.showEmpty(
            title = "暂无数据",
            btnText = null
        )
    }

    private fun showError(message: String) {
        hideStickyHeader()
        binding.stateLayout.showError(message)
    }

    override fun onDestroyView() {
        saveCurrentScrollState()
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val CATEGORY_CLICK_SCROLL_SUPPRESS_MS = 200L
    }
}
