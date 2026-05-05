package com.ggg.kt.wanandroidbyyohen.ui.square

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ggg.kt.wanandroidbyyohen.data.model.SquareTag

class SquarePagerAdapter(
    fragment: Fragment,
    private val tags: List<SquareTag>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tags.size

    override fun createFragment(position: Int): Fragment {
        return SquareArticleListFragment.newInstance(tags[position])
    }
}
