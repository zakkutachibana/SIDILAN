package com.zak.sidilan.ui.trx.bookout

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityBookInTrxBinding
import com.zak.sidilan.databinding.ActivityBookOutTrxBinding
import com.zak.sidilan.util.BookInTrxPagerAdapter
import com.zak.sidilan.util.BookOutTrxPagerAdapter

class BookOutTrxActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookOutTrxBinding
    private lateinit var adapter: BookOutTrxPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookOutTrxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = BookOutTrxPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabs.selectTab(binding.tabs.getTabAt(position))
            }
        })
        setupView()
    }
    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.record_book_out)
        supportActionBar?.elevation = 0f

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}