package com.ggg.kt.wanandroidbyyohen.ui.project

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProjectPagerAdapter(
    fragment: Fragment,
    private val tabs: List<ProjectTab>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = tabs.size

    override fun createFragment(position: Int): Fragment {
        return ProjectListFragment.newInstance(tabs[position])
    }
}