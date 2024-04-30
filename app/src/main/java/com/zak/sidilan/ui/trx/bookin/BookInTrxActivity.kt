package com.zak.sidilan.ui.trx.bookin

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityBookInTrxBinding
import com.zak.sidilan.ui.trx.BookTrxViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

val bookInTrxActivityModule = module {
    factory { BookInTrxActivity() }
}
class BookInTrxActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookInTrxBinding
    private lateinit var adapter: BookInTrxPagerAdapter
    private val viewModel: BookTrxViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookInTrxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = BookInTrxPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager.currentItem = tab.position
                    viewModel.clearSelectedBooksList()
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
        supportActionBar?.title = getString(R.string.record_book_in)
        supportActionBar?.elevation = 0f
    }

    private fun showConfirmationDialog(callback: (Boolean) -> Unit) {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
            .setTitle(resources.getString(R.string.title_update_stock))
            .setIcon(R.drawable.ic_update)
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                callback(false)
                dialog.dismiss()
            }
            .setPositiveButton("Ya") { dialog, which ->
                callback(true)
                dialog.dismiss()
            }
            .show()
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