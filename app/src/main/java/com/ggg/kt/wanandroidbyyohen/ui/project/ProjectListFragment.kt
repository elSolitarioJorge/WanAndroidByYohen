package com.ggg.kt.wanandroidbyyohen.ui.project

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
import com.ggg.kt.wanandroidbyyohen.data.model.ProjectTab
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentProjectListBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.launch

class ProjectListFragment : Fragment() {
    private var _binding: FragmentProjectListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProjectListViewModel by viewModels()

    private val projectAdapter by lazy {
        ProjectAdapter(
            onItemClick = { article ->
                ArticleNavigator.openArticle(requireContext(), article)
            },
            onCollectClick = { article ->
                viewModel.toggleCollect(article)
            }
        )
    }

    private val projectTab: ProjectTab by lazy {
        ProjectTab(
            title = requireArguments().getString(ARG_TITLE).orEmpty(),
            cid = if (requireArguments().containsKey(ARG_CID)) {
                requireArguments().getInt(ARG_CID)
            } else {
                null
            },
            isLatest = requireArguments().getBoolean(ARG_IS_LATEST)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTab(projectTab)
        initRecyclerView()
        initRefresh()
        initLoadMore()
        observeData()
        observeCollectStates()
        observeCollectErrors()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadIfNeeded()
    }

    private fun initRecyclerView() {
        binding.rvProjects.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProjects.adapter = projectAdapter
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
        binding.rvProjects.addLoadMoreListener {
            viewModel.loadMore()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.projectListState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            if (!binding.swipeRefresh.isRefreshing) {
                                binding.stateLayout.showLoading()
                            }
                        }

                        is UiState.Success -> {
                            binding.swipeRefresh.isRefreshing = false
                            projectAdapter.submitList(state.data.articles)
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

    private fun observeCollectStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectStates.collect { states ->
                    projectAdapter.updateCollectStates(states)
                }
            }
        }
    }

    private fun observeCollectErrors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectErrors.collect { message ->
                    Toast.makeText(
                        requireContext(),
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_CID = "arg_cid"
        private const val ARG_IS_LATEST = "arg_is_latest"

        fun newInstance(tab: ProjectTab): ProjectListFragment {
            return ProjectListFragment().apply {
                arguments = bundleOf(
                    ARG_TITLE to tab.title,
                    ARG_IS_LATEST to tab.isLatest
                ).apply {
                    tab.cid?.let {
                        putInt(ARG_CID, it)
                    }
                }
            }
        }
    }
}
