package com.zak.sidilan.ui.users

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zak.sidilan.ui.executivemenus.ChartFragment
import com.zak.sidilan.ui.executivemenus.TopTenFragment

class UserManagementPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RegisteredUserFragment()
            else -> {
                WhitelistFragment()
            }
        }
    }
}