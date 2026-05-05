package com.ggg.kt.wanandroidbyyohen.ui.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.extension.applyTopBarInsets
import com.ggg.kt.wanandroidbyyohen.data.model.ProjectTab
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentProjectBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch


class ProjectFragment : Fragment(R.layout.fragment_project) {
    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProjectViewModel by viewModels()

    private var tabLayoutMediator: TabLayoutMediator? = null
    private var currentTabs: List<ProjectTab> = emptyList()

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.saveSelectedPosition(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initInsets()
        initRefresh()
        observeData()
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
        viewModel.loadProjectTabsIfNeeded()
    }

    private fun initInsets() {
        binding.topBar.applyTopBarInsets()
    }

    private fun initRefresh() {
        binding.stateLayout.onRetryListener = {
            viewModel.refreshProjectTabs()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tabState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.stateLayout.showLoading()
                        }

                        is UiState.Success -> {
                            setupViewPager(state.data)
                            binding.stateLayout.showContent()
                        }

                        is UiState.Error -> {
                            binding.stateLayout.showError()
                        }
                    }
                }
            }
        }
    }

    private fun setupViewPager(tabs: List<ProjectTab>) {
        if (binding.viewPager.adapter != null && currentTabs == tabs) {
            restoreSelectedTab(tabs)
            return
        }

        tabLayoutMediator?.detach()
        binding.viewPager.adapter = ProjectPagerAdapter(this, tabs)
        currentTabs = tabs

        tabLayoutMediator = TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            tab.text = tabs[position].title
        }

        tabLayoutMediator?.attach()
        restoreSelectedTab(tabs)
    }

    private fun restoreSelectedTab(tabs: List<ProjectTab>) {
        if (tabs.isEmpty()) return

        binding.viewPager.setCurrentItem(
            viewModel.getSelectedPosition().coerceIn(tabs.indices),
            false
        )
    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        binding.viewPager.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
