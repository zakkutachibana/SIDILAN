package com.zak.sidilan.ui.users

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.ActivityUserManagementBinding
import com.zak.sidilan.util.HawkManager
import com.zak.sidilan.util.HelperFunction
import com.zak.sidilan.util.HelperFunction.parseUserRole
import com.zak.sidilan.util.UserRole
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

val userListActivityModule = module {
    factory { UserManagementActivity() }
}

class UserManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserManagementBinding
    private lateinit var adapter: UserManagementPagerAdapter
    private lateinit var hawkManager: HawkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserManagementPagerAdapter(supportFragmentManager, lifecycle)
        hawkManager = HawkManager(this)
        val role = hawkManager.retrieveData<User>("user")?.role.toString()

        checkRole(parseUserRole(role))
        setupView()
    }

    private fun checkRole(role: UserRole?) {
        when (role) {
            UserRole.ADMIN -> { }
            UserRole.DIRECTOR -> { }
            else -> {  }
        }
    }

    private fun setupView() {
        binding.viewPager.adapter = adapter
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabs.selectTab(binding.tabs.getTabAt(position))
            }
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Manajemen Pengguna"
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