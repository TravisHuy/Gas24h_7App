package com.nhathuy.gas24h_7app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewpagerAdapter(private val fragments:List<Fragment>,
                            fm:FragmentManager,
                            lifecycle: Lifecycle):FragmentStateAdapter(fm,lifecycle){
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
    fun getFragment(position: Int): Fragment {
        return fragments[position]
    }
}