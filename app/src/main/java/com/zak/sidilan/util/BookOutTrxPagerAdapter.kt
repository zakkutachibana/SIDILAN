package com.zak.sidilan.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zak.sidilan.ui.trx.bookin.BookInTrxOtherFragment
import com.zak.sidilan.ui.trx.bookin.BookInTrxPrintFragment
import com.zak.sidilan.ui.trx.bookout.BookOutTrxOtherFragment
import com.zak.sidilan.ui.trx.bookout.BookOutTrxSellFragment

class BookOutTrxPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BookOutTrxSellFragment()
            else -> {
                BookOutTrxOtherFragment()
            }
        }

    }
}