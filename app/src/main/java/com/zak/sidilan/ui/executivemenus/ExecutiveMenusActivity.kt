package com.zak.sidilan.ui.executivemenus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityExecutiveMenusBinding

class ExecutiveMenusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExecutiveMenusBinding
    private lateinit var adapter: ExecutiveMenusPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExecutiveMenusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ExecutiveMenusPagerAdapter(supportFragmentManager, lifecycle)
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
        supportActionBar?.title = getString(R.string.title_executive_charts)
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