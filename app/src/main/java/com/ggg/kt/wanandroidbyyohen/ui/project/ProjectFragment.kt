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
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentProjectBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch


class ProjectFragment : Fragment(R.layout.fragment_project) {
    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProjectViewModel by viewModels()

    private var tabLayoutMediator: TabLayoutMediator? = null

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

        observeData()
        viewModel.loadProjectTabs()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tabState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.contentGroup.visibility = View.GONE
                            binding.tvState.visibility = View.VISIBLE
                            binding.tvState.text = "加载中..."
                        }

                        is UiState.Success -> {
                            binding.tvState.visibility = View.GONE
                            binding.contentGroup.visibility = View.VISIBLE
                            setupViewPager(state.data)
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

    private fun setupViewPager(tabs: List<ProjectTab>) {
        binding.viewPager.adapter = ProjectPagerAdapter(this, tabs)

        tabLayoutMediator?.detach()
        tabLayoutMediator = TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            tab.text = tabs[position].title
        }

        tabLayoutMediator?.attach()
    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        super.onDestroyView()
        _binding = null
    }
}