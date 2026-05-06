package com.ggg.kt.wanandroidbyyohen.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.extension.addLoadMoreListener
import com.ggg.kt.wanandroidbyyohen.common.extension.applyTopBarInsets
import com.ggg.kt.wanandroidbyyohen.data.model.Banner
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentHomeBinding
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleAdapter
import com.ggg.kt.wanandroidbyyohen.ui.common.ArticleNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private var isBannerTouching = false
    private var isBannerScrolling = false
    private var hasRenderedBanners = false

    private val bannerPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            isBannerScrolling = state != ViewPager2.SCROLL_STATE_IDLE
        }
    }

    private val bannerTouchListener = object : RecyclerView.SimpleOnItemTouchListener() {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            isBannerTouching = when (e.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> true

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> false

                else -> isBannerTouching
            }
            return false
        }
    }

    private val bannerAdapter by lazy {
        BannerAdapter(
            onBannerClick = { banner ->
                ArticleNavigator.openWebView(
                    context = requireContext(),
                    title = banner.title,
                    url = banner.url
                )
            }
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInsets()
        initBanner()
        initRecyclerView()
        observeData()
        initRefresh()
        initLoadMore()
        initClick()
        observeCollectState()
        startBannerAutoScroll()
        viewModel.loadHomeDataIfNeeded()
    }

    private fun initInsets() {
        binding.searchContainer.applyTopBarInsets()
    }

    private fun initClick() {
        binding.stateLayout.onRetryListener = {
            viewModel.refreshHomeData()
        }
        binding.tvSearchEntry.setOnClickListener {
            findNavController().navigate(R.id.search_fragment)
        }
    }

    private fun initRecyclerView() {
        binding.rvArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticles.adapter = articleAdapter
    }

    private fun initBanner() {
        binding.cvBannerContainer.visibility = View.GONE
        binding.vpBanner.adapter = bannerAdapter
        binding.vpBanner.registerOnPageChangeCallback(bannerPageChangeCallback)
        getBannerRecyclerView()?.addOnItemTouchListener(bannerTouchListener)
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeState.collect { state ->
                    when(state) {
                        is UiState.Loading -> {
                            if (!binding.swipeRefresh.isRefreshing) {
                                binding.stateLayout.showLoading()
                            }
                        }

                        is UiState.Success -> {
                            binding.swipeRefresh.isRefreshing = false
                            val homeData = state.data
                            if (homeData.isRefresh || !hasRenderedBanners) {
                                updateBanners(homeData.banners)
                            }
                            articleAdapter.submitList(homeData.articles)
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

    private fun updateBanners(banners: List<Banner>) {
        hasRenderedBanners = true
        bannerAdapter.submitList(banners)

        if (banners.isEmpty()) {
            binding.cvBannerContainer.visibility = View.GONE
            return
        }

        binding.cvBannerContainer.visibility = View.VISIBLE
        binding.vpBanner.setCurrentItem(bannerAdapter.getInitialPosition(), false)
    }

    private fun startBannerAutoScroll() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    delay(BANNER_AUTO_SCROLL_INTERVAL)
                    if (canAutoScrollBanner()) {
                        binding.vpBanner.setCurrentItem(
                            binding.vpBanner.currentItem + 1,
                            true
                        )
                    }
                }
            }
        }
    }

    private fun canAutoScrollBanner(): Boolean {
        return bannerAdapter.realItemCount > 1 &&
            !isBannerTouching &&
            !isBannerScrolling
    }

    private fun getBannerRecyclerView(): RecyclerView? {
        return binding.vpBanner.getChildAt(0) as? RecyclerView
    }

    private fun initRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshHomeData()
        }
    }

    private fun initLoadMore() {
        binding.rvArticles.addLoadMoreListener {
            viewModel.loadMoreArticles()
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
        getBannerRecyclerView()?.removeOnItemTouchListener(bannerTouchListener)
        binding.vpBanner.unregisterOnPageChangeCallback(bannerPageChangeCallback)
        binding.vpBanner.adapter = null
        hasRenderedBanners = false
        _binding = null
    }

    companion object {
        private const val BANNER_AUTO_SCROLL_INTERVAL = 3_000L
    }
}
