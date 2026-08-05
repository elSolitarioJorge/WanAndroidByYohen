package com.ggg.kt.wanandroidbyyohen.ui.square

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.extension.applyTopBarInsets
import com.ggg.kt.wanandroidbyyohen.data.local.UserStore
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentSquareBinding
import kotlinx.coroutines.launch

class SquareFragment : Fragment(R.layout.fragment_square) {
    private var _binding: FragmentSquareBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SquareViewModel by viewModels()
    private var pagerAdapter: SquarePagerAdapter? = null
    private var tagLayoutManager: LinearLayoutManager? = null

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val tag = viewModel.tags.getOrNull(position) ?: return

            viewModel.selectPosition(position)
            tagAdapter.selectTag(tag)
            scrollSelectedTagToFront(position)
        }
    }

    private val tagAdapter by lazy {
        SquareTagAdapter(
            onTagClick = { tag ->
                val position = viewModel.tags.indexOfFirst { it.key == tag.key }
                if (position == -1) return@SquareTagAdapter

                viewModel.selectPosition(position)
                scrollSelectedTagToFront(position)
                binding.vpSquarePages.setCurrentItem(position, false)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSquareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initInsets()
        initTags()
        initViewPager()
        initClick()
    }

    private fun initInsets() {
        binding.llTopFixedArea.applyTopBarInsets()
    }

    private fun initTags() {
        tagLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvTags.layoutManager = tagLayoutManager
        binding.rvTags.adapter = tagAdapter
        tagAdapter.submitList(viewModel.tags)
        tagAdapter.selectTag(viewModel.getSelectedTag())
        binding.rvTags.post {
            scrollSelectedTagToFront(viewModel.getSelectedPosition())
        }
    }

    private fun initViewPager() {
        pagerAdapter = SquarePagerAdapter(this, viewModel.tags)
        binding.vpSquarePages.adapter = pagerAdapter
        binding.vpSquarePages.offscreenPageLimit = viewModel.tags.size
        binding.vpSquarePages.isUserInputEnabled = false
        binding.vpSquarePages.registerOnPageChangeCallback(pageChangeCallback)
        binding.vpSquarePages.setCurrentItem(
            viewModel.getSelectedPosition(),
            false
        )
    }

    private fun initClick() {
        binding.btnShareArticle.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (!UserStore.isLogin()) {
                    findNavController().navigate(R.id.login_fragment)
                    return@launch
                }

                findNavController().navigate(R.id.share_article_fragment)
            }
        }
    }

    private fun scrollSelectedTagToFront(position: Int) {
        val targetPosition = (position - 1).coerceAtLeast(0)
        val offset = binding.rvTags.paddingStart
        tagLayoutManager?.scrollToPositionWithOffset(targetPosition, offset)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vpSquarePages.unregisterOnPageChangeCallback(pageChangeCallback)
        binding.vpSquarePages.adapter = null
        binding.rvTags.adapter = null
        tagLayoutManager = null
        pagerAdapter = null
        _binding = null
    }
}
